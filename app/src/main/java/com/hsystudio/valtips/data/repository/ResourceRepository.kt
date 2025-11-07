package com.hsystudio.valtips.data.repository

import android.util.Log
import androidx.room.withTransaction
import com.hsystudio.valtips.data.local.AppPrefsManager
import com.hsystudio.valtips.data.local.db.AppDatabase
import com.hsystudio.valtips.data.mapper.toEntity
import com.hsystudio.valtips.data.remote.api.ResourceApi
import com.hsystudio.valtips.util.ImageDownloader
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourceRepository @Inject constructor(
    private val api: ResourceApi,
    private val db: AppDatabase,
    private val prefsManager: AppPrefsManager,
    private val imageDownloader: ImageDownloader
) {
    private val tag = "ResourceRepository"

    /** 리소스 용량 정보 조회 (MB) */
    suspend fun getResourceSize(): Result<Double> =
        runCatching {
            api.getResourceInfo().totalSizeMegabytes
        }.onFailure {
            Log.e(tag, "getResourceSize() 실패", it)
        }

    /** 최초 전체 동기화: 이미지 강제 재다운로드 */
    suspend fun initialSync(
        onProgressBytes: (
            (
                doneItems: Int,
                totalItems: Int,
                bytesRead: Long,
                totalBytes: Long,
                bytesPerSec: Long
            ) -> Unit
        )? = null
    ): Result<Unit> = runCatching {
        val agents = api.getAgents()
        val maps = api.getMaps()
        val tiers  = api.getTiers()
        val gameModes = api.getGameModes()
        val act = api.getCurrentAct()

        // 1) 이미지 작업
        val imageTasks = buildList {
            // Agents
            agents.forEach { a ->
                a.displayIcon?.let { add(it to "agent_${a.uuid}_icon.png") }
                a.fullPortrait?.let { add(it to "agent_${a.uuid}_portrait.png") }
                a.role.displayIcon?.let { add(it to "role_${a.role.uuid}_icon.png") }
                a.abilities.forEach { ab ->
                    ab.displayIcon?.let { add(it to "ability_${ab.id}.png") }
                }
            }
            // Maps
            maps.forEach { m ->
                m.displayIcon?.let { add(it to "map_${m.uuid}_icon.png") }
                m.listViewIcon?.let { add(it to "map_${m.uuid}_list.png") }
                m.splash?.let { add(it to "map_${m.uuid}_splash.png") }
            }
            // Tiers
            tiers.forEach { t ->
                t.largeIcon?.let { add(it to "tier_${t.tier}_large.png") }
            }
            // Game Modes
            gameModes.forEach { gm ->
                gm.displayIcon?.let { add(it to "gm_${gm.uuid}_icon.png") }
            }
        }

        // 2) 다운로드 (강제 새로 받기)
        val downloaded = imageDownloader.downloadAll(
            tasks = imageTasks,
            concurrency = 6,
            forceRefresh = true,
            onProgress = onProgressBytes
        )

        // 3) DB 반영
        db.withTransaction {
            db.abilityDao().clearAll()
            db.agentDao().clearAll()
            db.roleDao().clearAll()
            db.mapCalloutDao().clearAll()
            db.mapDao().clearAll()
            db.tierDao().clearAll()
            db.gameModeDao().clearAll()
            db.actDao().clearAll()

            // Roles
            val roleEntities = agents.map { it.role }
                .distinctBy { it.uuid }
                .map { r ->
                    r.toEntity(localIconPath = r.displayIcon?.let { downloaded[it] })
                }
            db.roleDao().upsert(roleEntities)

            // Agents
            val agentEntities = agents.map { a ->
                a.toEntity(
                    roleUuid = a.role.uuid,
                    localIconPath = a.displayIcon?.let { downloaded[it] },
                    localPortraitPath = a.fullPortrait?.let { downloaded[it] }
                )
            }
            db.agentDao().upsert(agentEntities)

            // Abilities
            val abilityEntities = agents.flatMap { a ->
                a.abilities.map { ab ->
                    ab.toEntity(
                        agentUuid = a.uuid,
                        localIconPath = ab.displayIcon?.let { downloaded[it] }
                    )
                }
            }
            db.abilityDao().upsert(abilityEntities)

            // Maps
            val mapEntities = maps.map { m ->
                m.toEntity(
                    localDisplayIconPath = m.displayIcon?.let { downloaded[it] },
                    localListIconPath = m.listViewIcon?.let { downloaded[it] },
                    localSplashPath = m.splash?.let { downloaded[it] }
                )
            }
            db.mapDao().upsert(mapEntities)

            // Callout
            val calloutEntities = maps.flatMap { m ->
                m.callouts.map { co -> co.toEntity(mapUuid = m.uuid) }
            }
            db.mapCalloutDao().upsert(calloutEntities)

            // Tiers
            val tierEntities = tiers.map { t ->
                t.toEntity(localIconPath = t.largeIcon?.let { downloaded[it] })
            }
            db.tierDao().upsert(tierEntities)

            // Game Modes
            val gmEntities = gameModes.map { gm ->
                gm.toEntity(localIconPath = gm.displayIcon?.let { downloaded[it] })
            }
            db.gameModeDao().upsert(gmEntities)

            // Act
            db.actDao().upsert(act.toEntity())
        }

        // 4) 최신 타임스탬프
        val updateInfo = api.getUpdatesInfo()
        prefsManager.setLastSync(updateInfo.latestTimestamp)
    }.onFailure {
        Log.e(tag, "initialSync() 실패", it)
    }

    /** 델타 동기화: 업데이트 있을 때만 강제 재다운로드 */
    suspend fun deltaSync(
        onProgressBytes: (
            (
                doneItems: Int,
                totalItems: Int,
                bytesRead: Long,
                totalBytes: Long,
                bytesPerSec: Long
            ) -> Unit
        )? = null
    ): Result<Boolean> = runCatching {
        // 버전 정보 없으면 종료
        val lastSync = prefsManager.lastSyncFlow.firstOrNull() ?: return@runCatching false
        val updates = api.getUpdatesInfo(lastSync)

        val hasUpdates = listOf(
            updates.updateCounts.agents,
            updates.updateCounts.maps,
            updates.updateCounts.tiers,
            updates.updateCounts.gameModes,
            updates.updateCounts.acts
        ).any { it > 0 }
        // 업데이트 내용 없으면 종료
        if (!hasUpdates) {
            prefsManager.setLastSync(updates.latestTimestamp)
            return@runCatching false
        }

        val delta = api.getDelta(lastSync)
        // 1) 이미지 작업
        val imageTasks = buildList {
            // Agents
            delta.agents.forEach { a ->
                a.displayIcon?.let { add(it to "agent_${a.uuid}_icon.png") }
                a.fullPortrait?.let { add(it to "agent_${a.uuid}_portrait.png") }
                a.role.displayIcon?.let { add(it to "role_${a.role.uuid}_icon.png") }
                a.abilities.forEach { ab ->
                    ab.displayIcon?.let { add(it to "ability_${ab.id}.png") }
                }
            }
            // Maps
            delta.maps.forEach { m ->
                m.displayIcon?.let { add(it to "map_${m.uuid}_icon.png") }
                m.listViewIcon?.let { add(it to "map_${m.uuid}_list.png") }
                m.splash?.let { add(it to "map_${m.uuid}_splash.png") }
            }
            // Tiers
            delta.tiers.forEach { t ->
                t.largeIcon?.let { add(it to "tier_${t.tier}_large.png") }
            }
            // Game Modes
            delta.gameModes.forEach { gm ->
                gm.displayIcon?.let { add(it to "gm_${gm.uuid}_icon.png") }
            }
        }

        // 2) 다운로드 (강제 새로 받기)
        val downloaded = if (imageTasks.isNotEmpty()) {
            imageDownloader.downloadAll(
                tasks = imageTasks,
                concurrency = 6,
                forceRefresh = true,
                onProgress = onProgressBytes
            )
        } else {
            emptyMap()
        }

        // 3) DB 반영
        db.withTransaction {
            // Agents
            if (delta.agents.isNotEmpty()) {
                // Roles
                val roles = delta.agents.map { it.role }
                    .distinctBy { it.uuid }
                    .map { r ->
                        r.toEntity(localIconPath = r.displayIcon?.let { downloaded[it] })
                    }
                db.roleDao().upsert(roles)

                // Agents
                val agents = delta.agents.map { a ->
                    a.toEntity(
                        roleUuid = a.role.uuid,
                        localIconPath = a.displayIcon?.let { downloaded[it] },
                        localPortraitPath = a.fullPortrait?.let { downloaded[it] }
                    )
                }
                db.agentDao().upsert(agents)

                // Abilities
                val abilities = delta.agents.flatMap { a ->
                    a.abilities.map { ab ->
                        ab.toEntity(
                            agentUuid = a.uuid,
                            localIconPath = ab.displayIcon?.let { downloaded[it] }
                        )
                    }
                }
                db.abilityDao().upsert(abilities)
            }

            // Maps
            if (delta.maps.isNotEmpty()) {
                // 각 맵 callouts은 전체 교체
                delta.maps.forEach { m ->
                    db.mapCalloutDao().clearByMap(m.uuid)
                }
                // Maps
                val maps = delta.maps.map { m ->
                    m.toEntity(
                        localDisplayIconPath = m.displayIcon?.let { downloaded[it] },
                        localListIconPath = m.listViewIcon?.let { downloaded[it] },
                        localSplashPath = m.splash?.let { downloaded[it] }
                    )
                }
                db.mapDao().upsert(maps)

                // Callouts
                val callouts = delta.maps.flatMap { m ->
                    m.callouts.map { co -> co.toEntity(mapUuid = m.uuid) }
                }
                db.mapCalloutDao().upsert(callouts)
            }

            // tiers
            if (delta.tiers.isNotEmpty()) {
                val tierEntities = delta.tiers.map { t ->
                    t.toEntity(localIconPath = t.largeIcon?.let { downloaded[it] })
                }
                db.tierDao().upsert(tierEntities)
            }

            // game modes
            if (delta.gameModes.isNotEmpty()) {
                val gmEntities = delta.gameModes.map { gm ->
                    gm.toEntity(localIconPath = gm.displayIcon?.let { downloaded[it] })
                }
                db.gameModeDao().upsert(gmEntities)
            }

            // Act
            val actEntity = delta.acts.firstOrNull()?.toEntity()
            if (actEntity != null) db.actDao().upsert(actEntity)
        }

        // 4) 최신 타임스탬프
        prefsManager.setLastSync(updates.latestTimestamp)
        true
    }.onFailure {
        Log.e(tag, "deltaSync() 실패", it)
    }
}
