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
    private val prefs: AppPrefsManager,
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

        // 1) 이미지 작업
        val imageTasks = buildList {
            agents.forEach { a ->
                a.displayIcon?.let { add(it to "agent_${a.uuid}_icon.png") }
                a.fullPortrait?.let { add(it to "agent_${a.uuid}_portrait.png") }
                a.role.displayIcon?.let { add(it to "role_${a.role.uuid}_icon.png") }
                a.abilities.forEach { ab ->
                    ab.displayIcon?.let { add(it to "ability_${ab.id}.png") }
                }
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

            val roles = agents.map { it.role }.distinctBy { it.uuid }.map { r ->
                r.toEntity(localIconPath = r.displayIcon?.let { downloaded[it] })
            }
            db.roleDao().upsert(roles)

            val agentEntities = agents.map { a ->
                a.toEntity(
                    roleUuid = a.role.uuid,
                    localIconPath = a.displayIcon?.let { downloaded[it] },
                    localPortraitPath = a.fullPortrait?.let { downloaded[it] }
                )
            }
            db.agentDao().upsert(agentEntities)

            val abilityEntities = agents.flatMap { a ->
                a.abilities.map { ab ->
                    ab.toEntity(
                        agentUuid = a.uuid,
                        localIconPath = ab.displayIcon?.let { downloaded[it] }
                    )
                }
            }
            db.abilityDao().upsert(abilityEntities)
        }

        // 4) 최신 타임스탬프
        val updateInfo = api.getUpdatesInfo()
        prefs.setLastSync(updateInfo.latestTimestamp)
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
        val lastSync = prefs.lastSyncFlow.firstOrNull() ?: return@runCatching false
        val updates = api.getUpdatesInfo(lastSync)

        val hasUpdates = listOf(
            updates.updateCounts.agents,
            updates.updateCounts.maps,
            updates.updateCounts.tiers,
            updates.updateCounts.gameModes
        ).any { it > 0 }
        // 업데이트 내용 없으면 종료
        if (!hasUpdates) {
            prefs.setLastSync(updates.latestTimestamp)
            return@runCatching false
        }

        val delta = api.getDelta(lastSync)

        // 1) 이미지 작업
        if (delta.agents.isNotEmpty()) {
            val imageTasks = buildList {
                delta.agents.forEach { a ->
                    a.displayIcon?.let { add(it to "agent_${a.uuid}_icon.png") }
                    a.fullPortrait?.let { add(it to "agent_${a.uuid}_portrait.png") }
                    a.role.displayIcon?.let { add(it to "role_${a.role.uuid}_icon.png") }
                    a.abilities.forEach { ab ->
                        ab.displayIcon?.let { add(it to "ability_${ab.id}.png") }
                    }
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
                val roles = delta.agents.map { it.role }
                    .distinctBy { it.uuid }
                    .map { r -> r.toEntity(localIconPath = r.displayIcon?.let { downloaded[it] }) }
                db.roleDao().upsert(roles)

                val agents = delta.agents.map { a ->
                    a.toEntity(
                        roleUuid = a.role.uuid,
                        localIconPath = a.displayIcon?.let { downloaded[it] },
                        localPortraitPath = a.fullPortrait?.let { downloaded[it] }
                    )
                }
                db.agentDao().upsert(agents)

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
        }

        // 4) 최신 타임스탬프
        prefs.setLastSync(updates.latestTimestamp)
        true
    }.onFailure {
        Log.e(tag, "deltaSync() 실패", it)
    }
}
