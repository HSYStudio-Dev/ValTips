package com.hsystudio.valtips.domain.repository

import com.hsystudio.valtips.domain.model.MapListItem
import com.hsystudio.valtips.feature.map.model.MapDetailUiState
import kotlinx.coroutines.flow.Flow

interface MapRepository {
    // 맵 전체에서 리스트 카드에 필요한 정보만 실시간 관찰
    fun observeMapCards(): Flow<List<MapListItem>>

    // 현재 액트 표시
    fun observeCurrentActName(): Flow<String?>

    // 맵 상세 정보 실시간 관찰
    fun observeMapDetail(mapUuid: String): Flow<MapDetailUiState?>
}
