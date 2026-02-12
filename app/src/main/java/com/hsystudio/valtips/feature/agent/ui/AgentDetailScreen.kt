package com.hsystudio.valtips.feature.agent.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.hsystudio.valtips.R
import com.hsystudio.valtips.domain.model.NativeAdUiState
import com.hsystudio.valtips.feature.agent.viewmodel.AgentDetailViewModel
import com.hsystudio.valtips.ui.component.BorderButton
import com.hsystudio.valtips.ui.component.IconChip
import com.hsystudio.valtips.ui.component.ad.AdErrorPlaceholder
import com.hsystudio.valtips.ui.component.ad.AdLoadingPlaceholder
import com.hsystudio.valtips.ui.component.ad.NativeAdBanner
import com.hsystudio.valtips.ui.component.bar.AppTopBar
import com.hsystudio.valtips.ui.theme.ColorBlack
import com.hsystudio.valtips.ui.theme.ColorMint
import com.hsystudio.valtips.ui.theme.ColorRed
import com.hsystudio.valtips.ui.theme.ColorStroke
import com.hsystudio.valtips.ui.theme.GradientMint
import com.hsystudio.valtips.ui.theme.TextGray
import com.hsystudio.valtips.ui.theme.TextWhite
import com.hsystudio.valtips.util.toCoilModel

@Composable
fun AgentDetailScreen(
    onBack: () -> Unit,
    onGuideClick: (agentUuid: String) -> Unit,
    viewModel: AgentDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val adState by viewModel.nativeAdState.collectAsStateWithLifecycle()
    val isProMember by viewModel.isProMember.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Details",
                onNavClick = onBack
            )
        }
    ) { values ->
        when (val data = uiState) {
            null -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = values.calculateTopPadding()),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = TextGray)
            }

            else -> {
                val scroll = rememberScrollState()
                // 스킬 선택 상태
                var selectedIdx by rememberSaveable { mutableIntStateOf(-1) }
                // 스킬 상세 토글 상태
                var showDetail by rememberSaveable { mutableStateOf(false) }
                // 요원 이미지
                val portraitModel = remember(data.portraitLocal) { toCoilModel(data.portraitLocal) }

                LaunchedEffect(selectedIdx) {
                    showDetail = false
                }

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = values.calculateTopPadding())
                ) {
                    val horizontalPadding = when {
                        maxWidth < 400.dp -> 24.dp
                        maxWidth < 600.dp -> 32.dp
                        else -> 40.dp
                    }
                    val verticalPadding = when {
                        maxHeight < 600.dp -> 24.dp
                        maxHeight < 800.dp -> 32.dp
                        else -> 40.dp
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scroll)
                            .padding(horizontal = horizontalPadding)
                            .padding(bottom = verticalPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        AsyncImage(
                            model = portraitModel,
                            contentDescription = "요원 이미지"
                        )

                        Spacer(Modifier.height(16.dp))

                        // 역할 이미지 + 이름
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 역할 이미지
                            data.roleIconLocal?.let {
                                AsyncImage(
                                    model = toCoilModel(it),
                                    contentDescription = "역할 아이콘",
                                    modifier = Modifier.size(24.dp),
                                    contentScale = ContentScale.Fit
                                )
                                Spacer(Modifier.width(8.dp))
                            }
                            // 이름
                            Text(
                                text = data.name,
                                style = MaterialTheme.typography.headlineLarge,
                                color = TextWhite
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        // 역할 + 출신
                        Text(
                            text = listOfNotNull(
                                data.roleName?.let { "역할: $it" },
                                data.origin?.let { "출신: $it" }
                            ).joinToString("  /  "),
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextWhite,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(16.dp))

                        // 요원 설명
                        data.description?.takeIf { it.isNotBlank() }?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextWhite,
                                textAlign = TextAlign.Left
                            )
                        }

                        // 광고 배너
                        if (!isProMember) {
                            Spacer(Modifier.height(16.dp))

                            Box(modifier = Modifier.fillMaxWidth()) {
                                when (adState) {
                                    is NativeAdUiState.Loading -> {
                                        AdLoadingPlaceholder()
                                    }
                                    is NativeAdUiState.Error -> {
                                        AdErrorPlaceholder()
                                    }
                                    is NativeAdUiState.Success -> {
                                        NativeAdBanner(
                                            nativeAd = (adState as NativeAdUiState.Success).ad,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }

                        // 경계선
                        HorizontalDivider(
                            color = TextGray,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )

                        // 스킬 칩
                        if (data.abilities.isNotEmpty()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                itemsIndexed(
                                    items = data.abilities,
                                    key = { _, ab -> "${ab.slot}-${ab.name}" }
                                ) { index, ability ->
                                    IconChip(
                                        isSelected = selectedIdx == index,
                                        isAll = false,
                                        iconLocal = ability.iconLocal,
                                        labelWhenNoIcon = if (
                                            ability.slot == "Passive" &&
                                            ability.iconLocal.isNullOrBlank()
                                        ) {
                                            "P"
                                        } else {
                                            null
                                        },
                                        btnSize = 64.dp,
                                        iconSize = 48.dp,
                                        onClick = {
                                            selectedIdx = if (selectedIdx == index) -1 else index
                                        }
                                    )
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            // 스킬 설명 카드(애니메이션 적용)
                            AnimatedVisibility(
                                visible = selectedIdx in data.abilities.indices,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                // 선택된 칩이 바뀔 때 카드 내용 전환 애니메이션
                                AnimatedContent(
                                    targetState = selectedIdx,
                                    transitionSpec = {
                                        (fadeIn() + slideInVertically { it / 3 }) togetherWith
                                            (fadeOut() + slideOutVertically { -it / 3 })
                                    },
                                    label = "SkillCardSwitch"
                                ) { sel ->
                                    if (sel != -1) {
                                        val selected = data.abilities.getOrNull(sel)
                                            ?: return@AnimatedContent
                                        // 설명 카드
                                        Card(
                                            shape = RoundedCornerShape(8.dp),
                                            border = BorderStroke(1.dp, ColorStroke),
                                            colors = CardDefaults.cardColors(containerColor = ColorBlack),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            // 설명 카드 컨텐츠
                                            Column(
                                                Modifier.padding(16.dp),
                                                verticalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                // 스킬명 + 상세 스위치 버튼
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    // 스킬명
                                                    Text(
                                                        text = "${selected.name} (${selected.slot})",
                                                        style = MaterialTheme.typography.headlineSmall,
                                                        color = ColorRed
                                                    )
                                                    // 상세 스위치 버튼
                                                    val enabled = !selected.details.isNullOrBlank()
                                                    val rotation by animateFloatAsState(
                                                        targetValue = if (showDetail) 180f else 0f,
                                                        animationSpec = tween(durationMillis = 180),
                                                        label = "chipIconRotation"
                                                    )

                                                    val chipBrush = Brush.horizontalGradient(
                                                        colors = listOf(ColorMint, GradientMint)
                                                    )
                                                    val chipShape = RoundedCornerShape(20.dp)

                                                    Box(
                                                        modifier = Modifier
                                                            .height(40.dp)
                                                            .clip(chipShape)
                                                            .background(chipBrush)
                                                            .border(
                                                                width = 0.5.dp,
                                                                color = TextWhite.copy(
                                                                    alpha = if (enabled) {
                                                                        1f
                                                                    } else {
                                                                        0.35f
                                                                    }
                                                                ),
                                                                shape = chipShape
                                                            )
                                                            .padding(horizontal = 1.dp, vertical = 1.dp)
                                                    ) {
                                                        AssistChip(
                                                            onClick = {
                                                                if (!selected.details.isNullOrBlank()) {
                                                                    showDetail = !showDetail
                                                                }
                                                            },
                                                            label = {
                                                                Text(
                                                                    text = if (showDetail) "기본" else "상세",
                                                                    style = MaterialTheme.typography.bodyMedium
                                                                )
                                                            },
                                                            enabled = enabled,
                                                            trailingIcon = {
                                                                Icon(
                                                                    painter = painterResource(R.drawable.ic_change),
                                                                    contentDescription = "전환",
                                                                    modifier = Modifier
                                                                        .size(20.dp)
                                                                        .rotate(rotation)
                                                                )
                                                            },
                                                            colors = AssistChipDefaults.assistChipColors(
                                                                containerColor = Color.Transparent,
                                                                labelColor = TextWhite,
                                                                trailingIconContentColor = TextWhite,
                                                                disabledContainerColor = Color.Transparent,
                                                                disabledLabelColor = TextWhite.copy(alpha = 0.6f),
                                                                disabledTrailingIconContentColor = TextWhite.copy(0.6f)
                                                            ),
                                                            border = null,
                                                            modifier = Modifier.height(40.dp)
                                                        )
                                                    }
                                                }

                                                // 스킬 설명
                                                Text(
                                                    text = if (showDetail && !selected.details.isNullOrBlank()) {
                                                        selected.details
                                                    } else {
                                                        (selected.description ?: "설명 없음")
                                                    },
                                                    style = if (showDetail && !selected.details.isNullOrBlank()) {
                                                        MaterialTheme.typography.bodyLarge
                                                    } else {
                                                        MaterialTheme.typography.bodyMedium
                                                    },
                                                    color = TextGray
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(32.dp))

                        // 맵 별 스킬 가이드 버튼
                        BorderButton(
                            text = "맵별 스킬 가이드",
                            onClick = { onGuideClick(data.uuid) }
                        )
                    }
                }
            }
        }
    }
}
