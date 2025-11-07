package com.hsystudio.valtips.domain.repository

import com.hsystudio.valtips.data.local.dao.ActDao
import com.hsystudio.valtips.data.local.dao.MapDao
import com.hsystudio.valtips.domain.model.MapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MapRepositoryImpl @Inject constructor(
    private val mapDao: MapDao,
    private val actDao: ActDao
) : MapRepository {
    // 맵 전체에서 리스트 카드에 필요한 정보만 실시간 관찰
    override fun observeMapCards(): Flow<List<MapListItem>> =
        mapDao.observeMapsWithCallouts().map { list ->
            list.map { mwc ->
                val m = mwc.map
                MapListItem(
                    uuid = m.uuid,
                    displayName = m.displayName,
                    englishName = m.englishName,
                    listImageLocal = m.listViewIconLocal ?: m.splashLocal,
                    isActiveInRotation = (m.isActiveInRotation == true)
                )
            }
        }

    // 현재 액트 표시
    override fun observeCurrentActName(): Flow<String?> =
        actDao.observeLatest().map { it?.displayName }
}
