package com.hsystudio.valtips.domain.repository

import com.hsystudio.valtips.data.local.dao.AgentDao
import com.hsystudio.valtips.data.mapper.toBase
import com.hsystudio.valtips.data.mapper.toDomain
import com.hsystudio.valtips.data.mapper.toUi
import com.hsystudio.valtips.data.remote.api.LineupApi
import com.hsystudio.valtips.feature.lineup.model.LineupCardBase
import com.hsystudio.valtips.feature.lineup.model.LineupCardItem
import com.hsystudio.valtips.feature.lineup.model.LineupDetailItem
import com.hsystudio.valtips.feature.lineup.model.LineupStatus
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class LineupRepositoryImpl @Inject constructor(
    private val api: LineupApi,
    private val agentDao: AgentDao
) : LineupRepository {
    // ─────────────────────────────────────────────
    // 라인업 존재 여부 조회
    // ─────────────────────────────────────────────

    // 특정 요원의 맵별 라인업 존재 여부 조회
    override suspend fun getMapsLineupStatus(agentUuid: String): Result<List<LineupStatus>> =
        runCatching {
            api.getMapLineupStatus(agentUuid).map { it.toDomain() }
        }

    // 특정 맵의 요원별 라인업 존재 여부 조회
    override suspend fun getAgentsLineupStatus(mapUuid: String): Result<List<LineupStatus>> =
        runCatching {
            api.getAgentLineupStatus(mapUuid).map { it.toDomain() }
        }

    // ─────────────────────────────────────────────
    // Lineups 캐시 (mapUuid + agentUuid 단위)
    // ─────────────────────────────────────────────

    // 라인업 캐시 키 (맵 + 요원 조합)
    private data class LineupKey(
        val mapUuid: String,
        val agentUuid: String
    )

    // 라인업 캐시 엔트리
    private data class LineupCacheEntry(
        var latestTimestamp: String,
        val items: MutableList<LineupCardItem>,
        var lastAccessMillis: Long
    )

    // LRU 최대 보관 개수
    private val maxCacheEntries = 20

    // 접근 순서 기반 LRU 캐시
    private val lineupCache =
        object : LinkedHashMap<LineupKey, LineupCacheEntry>(maxCacheEntries, 0.75f, true) {
            override fun removeEldestEntry(
                eldest: MutableMap.MutableEntry<LineupKey, LineupCacheEntry>
            ): Boolean = size > maxCacheEntries
        }

    // 캐시 동시 접근 보호
    private val cacheMutex = Mutex()

    // ─────────────────────────────────────────────
    // AgentAssets 캐시 (agentUuid 단위)
    // ─────────────────────────────────────────────

    // 요원 아이콘 + 스킬 아이콘 묶음
    private data class AgentAssets(
        val agentIconLocal: String?,
        val abilityIconBySlot: Map<String, String?>
    )

    // agentUuid → AgentAssets 캐시
    private val agentAssetsCache = mutableMapOf<String, AgentAssets>()

    // ─────────────────────────────────────────────
    // Lineups 조회 (캐시 + updates API)
    // ─────────────────────────────────────────────

    // 요원 + 맵 조합에 대한 라인업 목록 조회
    override suspend fun getLineups(
        agentUuid: String,
        mapUuid: String
    ): Result<List<LineupCardItem>> = runCatching {
        val key = LineupKey(mapUuid, agentUuid)
        val now = System.currentTimeMillis()

        // 라인업 캐시 스냅샷 조회
        val cached = cacheMutex.withLock {
            lineupCache[key]?.also { it.lastAccessMillis = now }
        }

        // 캐시 없는 경우 /lineups 전체 조회
        if (cached == null) {
            val response = api.getLineups(agentUuid = agentUuid, mapUuid = mapUuid)

            // DTO → Base 변환 (이미지 제외)
            val bases = response.data.map { it.toBase() }

            // Base + AgentAssets → 최종 UI 모델
            val items = attachImages(bases).toMutableList()

            // 캐시 저장
            cacheMutex.withLock {
                lineupCache[key] = LineupCacheEntry(
                    latestTimestamp = response.latestTimestamp,
                    items = items,
                    lastAccessMillis = now
                )
            }
            return@runCatching items
        }

        // 캐시 있는 경우 /lineups/updates 조회
        val updateResponse = api.getLineupUpdates(
            agentUuid = agentUuid,
            mapUuid = mapUuid,
            since = cached.latestTimestamp
        )

        // 변경분 있을 때만 병합
        if (updateResponse.data.isNotEmpty()) {
            val newBases = updateResponse.data.map { it.toBase() }
            val newItems = attachImages(newBases)

            return@runCatching cacheMutex.withLock {
                val entry = lineupCache[key] ?: cached

                // id 기준 중복 방지 병합
                val existingIds = entry.items.map { it.id }.toMutableSet()
                newItems.forEach { item ->
                    if (existingIds.add(item.id)) entry.items += item
                }

                entry.latestTimestamp = updateResponse.latestTimestamp
                entry.lastAccessMillis = now

                entry.items
            }
        }
        // 변경분 없는 경우 → 캐시 그대로 반환
        cacheMutex.withLock {
            lineupCache[key]?.also { it.lastAccessMillis = now }?.items ?: cached.items
        }
    }

    // ─────────────────────────────────────────────
    // Base → UI 모델 변환 + 이미지 연결
    // ─────────────────────────────────────────────

    // Base 리스트에 agent/ability 이미지 연결
    private suspend fun attachImages(
        bases: List<LineupCardBase>
    ): List<LineupCardItem> {
        if (bases.isEmpty()) return emptyList()

        // 필요한 agentUuid 목록
        val agentUuids = bases.map { it.agentUuid }.distinct()

        // agentUuid → AgentAssets 매핑
        val assetsMap = getAgentAssets(agentUuids)

        return bases.map { base ->
            val assets = assetsMap[base.agentUuid]
            val abilityIcon = assets?.abilityIconBySlot?.get(base.abilitySlotRaw)

            LineupCardItem(
                id = base.id,
                title = base.title,
                writer = base.writer,
                side = base.side,
                thumbnail = base.thumbnail,
                abilitySlot = base.abilitySlotUi,
                agentImage = assets?.agentIconLocal,
                abilityImage = abilityIcon
            )
        }
    }

    // ─────────────────────────────────────────────
    // AgentAssets 조회 (Repo 내부 캐시 + DAO)
    // ─────────────────────────────────────────────

    // agentUuid 목록에 대한 AgentAssets 조회
    private suspend fun getAgentAssets(
        agentUuids: List<String>
    ): Map<String, AgentAssets> {
        // 이미 캐시에 있는 것
        val cached = agentAssetsCache.filterKeys { it in agentUuids }

        // 캐시에 없는 agentUuid만 추출
        val missingUuids = agentUuids.filterNot { agentAssetsCache.containsKey(it) }

        // DB 조회가 필요한 경우만 IN 쿼리 실행
        if (missingUuids.isNotEmpty()) {
            val details = agentDao.getWithDetailsByUuids(missingUuids)

            details.forEach { detail ->
                val abilityMap = detail.abilities.associate { ab ->
                    ab.slot to ab.displayIconLocal
                }

                agentAssetsCache[detail.agent.uuid] = AgentAssets(
                    agentIconLocal = detail.agent.displayIconLocal,
                    abilityIconBySlot = abilityMap
                )
            }
        }
        return cached + agentAssetsCache.filterKeys { it in agentUuids }
    }

    // ─────────────────────────────────────────────
    // 라인업 상세 정보 조회
    // ─────────────────────────────────────────────

    override suspend fun getLineupDetail(lineupId: Int): Result<LineupDetailItem> =
        runCatching {
            api.getLineupDetail(lineupId).toUi()
        }
}
