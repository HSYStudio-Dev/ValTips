package com.hsystudio.valtips.feature.agent.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hsystudio.valtips.feature.agent.viewmodel.AgentsViewModel
import com.hsystudio.valtips.ui.component.AgentCard
import com.hsystudio.valtips.ui.component.IconChip
import com.hsystudio.valtips.ui.component.bar.AppTopBar

@Composable
fun AgentsScreen(
    onAgentClick: (String) -> Unit,
    viewModel: AgentsViewModel = hiltViewModel()
) {
    val agents by viewModel.agents.collectAsStateWithLifecycle()
    val roles by viewModel.roles.collectAsStateWithLifecycle()
    val selected by viewModel.selectedRoleUuid.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AppTopBar(title = "Agents")
        }
    ) { values ->
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
                    .padding(horizontal = horizontalPadding)
                    .padding(top = verticalPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // 역할 필터 버튼
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                ) {
                    items(count = minOf(5, roles.size)) { i ->
                        val item = roles[i]
                        IconChip(
                            isSelected = (selected ?: "") == item.uuid,
                            isAll = item.uuid.isEmpty(),
                            iconLocal = item.roleIconLocal,
                            onClick = { viewModel.selectRole(item.uuid) }
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // 요원 카드 그리드
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 64.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = verticalPadding),
                    modifier = Modifier.weight(1f)
                ) {
                    items(agents, key = { it.uuid }) { agent ->
                        AgentCard(
                            agent = agent,
                            onClick = onAgentClick
                        )
                    }
                }
            }
        }
    }
}
