package com.hsystudio.valtips.feature.agent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsystudio.valtips.domain.model.AgentCardItem
import com.hsystudio.valtips.domain.model.RoleFilterItem
import com.hsystudio.valtips.domain.repository.AgentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AgentsViewModel @Inject constructor(
    private val repo: AgentRepository
) : ViewModel() {
    // 현재 선택된 역할 UUID(null이면 전체)
    private val _selectedRoleUuid = MutableStateFlow<String?>(null)
    val selectedRoleUuid: StateFlow<String?> = _selectedRoleUuid

    // 역할 선택이 바뀔 때마다 해당 역할의 요원 목록을 Flow로 구독
    @OptIn(ExperimentalCoroutinesApi::class)
    val agents: StateFlow<List<AgentCardItem>> =
        _selectedRoleUuid
            .flatMapLatest { repo.observeAgents(it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // 역할 필터 목록 상태
    private val _roles = MutableStateFlow<List<RoleFilterItem>>(emptyList())
    val roles: StateFlow<List<RoleFilterItem>> = _roles

    // 최초 1회 역할 필터 목록 갱신
    init {
        viewModelScope.launch { _roles.value = repo.getRoleFilters() }
    }

    // 역할 선택
    fun selectRole(uuidOrEmpty: String) {
        _selectedRoleUuid.value = uuidOrEmpty.ifEmpty { null }
    }
}
