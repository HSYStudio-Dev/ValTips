package com.hsystudio.valtips.feature.map.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.hsystudio.valtips.feature.map.model.MapDetailUi
import com.hsystudio.valtips.ui.theme.ColorBlack
import com.hsystudio.valtips.ui.theme.ColorMint
import com.hsystudio.valtips.ui.theme.TextGray
import com.hsystudio.valtips.ui.theme.TextWhite
import com.hsystudio.valtips.util.toCoilModel

@Composable
fun MiniMapSection(
    data: MapDetailUi,
    isAttackerView: Boolean,
    showSmoke: Boolean,
    onSideChange: (Boolean) -> Unit,
    onSmokeChange: (Boolean) -> Unit
) {
    // 현재 선택된 미니맵 경로 결정
    val currentMiniMapLocal = remember(data, isAttackerView, showSmoke) {
        if (isAttackerView) {
            if (showSmoke && !data.miniMapAttackerSmokeLocal.isNullOrBlank()) {
                data.miniMapAttackerSmokeLocal
            } else {
                data.miniMapAttackerLocal
            }
        } else {
            if (showSmoke && !data.miniMapDefenderSmokeLocal.isNullOrBlank()) {
                data.miniMapDefenderSmokeLocal
            } else {
                data.miniMapDefenderLocal
            }
        }
    }
    val smokeAvailable = if (isAttackerView) data.hasAttackerSmoke else data.hasDefenderSmoke
    val miniMapModel = remember(currentMiniMapLocal) { toCoilModel(currentMiniMapLocal) }
    val splashModel = remember(data.splashLocal) { toCoilModel(data.splashLocal) }
    // 확대/이동 상태
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    // 확대 범위
    val minScale = 1f
    val maxScale = 3f

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
        ) {
            // 미니맵 이미지 박스 사이즈
            val containerW = constraints.maxWidth.toFloat()
            val containerH = constraints.maxHeight.toFloat()

            // 미니맵 카드
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = ColorBlack),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ColorBlack)
                ) {
                    // 스플래시 배경
                    if (data.splashLocal != null) {
                        AsyncImage(
                            model = splashModel,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            alpha = 0.2f
                        )
                    }
                    // 미니맵 이미지
                    if (currentMiniMapLocal != null) {
                        // 더블 탭 효과
                        val doubleTapModifier = Modifier.pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = {
                                    if (scale > 1f) {
                                        // 원래 크기로 복구
                                        scale = 1f
                                        offsetX = 0f
                                        offsetY = 0f
                                    } else {
                                        // 2배 확대
                                        scale = 2f
                                    }
                                }
                            )
                        }

                        // 줌/이동 처리 핸들러
                        val transformState = rememberTransformableState { zoomChange, panChange, _ ->
                            // 확대 비율 업데이트
                            val newScale = (scale * zoomChange).coerceIn(minScale, maxScale)
                            // 확대 비율에 따른 이동 범위 보정
                            val scaleRatio = if (scale != 0f) newScale / scale else 1f
                            // 확대 값 갱신
                            scale = newScale
                            // 이동 좌표 업데이트
                            offsetX += panChange.x * scaleRatio
                            offsetY += panChange.y * scaleRatio
                            // 이동 가능 범위
                            val limitX = (containerW * (scale - 1)) / 2f
                            val limitY = (containerH * (scale - 1)) / 2f
                            // 이동 범위 제한
                            offsetX = offsetX.coerceIn(-limitX, limitX)
                            offsetY = offsetY.coerceIn(-limitY, limitY)
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .then(doubleTapModifier)
                                .transformable(transformState),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = miniMapModel,
                                contentDescription = "미니맵",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .graphicsLayer(
                                        scaleX = scale,
                                        scaleY = scale,
                                        translationX = offsetX,
                                        translationY = offsetY
                                    ),
                                contentScale = ContentScale.Fit
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "미니맵이 아직 준비중입니다.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextGray
                            )
                        }
                    }
                }
            }
        }
        // 공격/수비 토글 + 연막 스위치
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 공격/수비 토글
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterToggleChip(
                    text = "공격",
                    selected = isAttackerView,
                    onClick = { onSideChange(true) }
                )
                FilterToggleChip(
                    text = "수비",
                    selected = !isAttackerView,
                    enabled = data.hasDefenderView || !data.hasAttackerView,
                    onClick = { onSideChange(false) }
                )
            }
            // 연막 스위치
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "추천 연막 보기",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (smokeAvailable) TextWhite else TextGray
                )
                Switch(
                    checked = showSmoke && smokeAvailable,
                    enabled = smokeAvailable,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = ColorMint,
                        checkedBorderColor = ColorMint
                    ),
                    onCheckedChange = {
                        if (smokeAvailable) onSmokeChange(!showSmoke)
                    }
                )
            }
        }
    }
}

@Composable
private fun FilterToggleChip(
    text: String,
    selected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val bg = when {
        !enabled -> ColorBlack
        selected -> ColorMint
        else -> Color.Transparent
    }
    val borderColor = when {
        !enabled -> TextGray
        selected -> ColorMint
        else -> TextGray
    }
    val textColor = when {
        !enabled -> TextGray
        selected -> ColorBlack
        else -> TextWhite
    }

    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        color = bg,
        border = BorderStroke(1.dp, borderColor),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = textColor
        )
    }
}
