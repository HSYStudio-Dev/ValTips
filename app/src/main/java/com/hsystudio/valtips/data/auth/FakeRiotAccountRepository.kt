package com.hsystudio.valtips.data.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

// RSO 연동 전 사용할 가짜 라이엇 계정 모델
data class FakeRiotAccount(
    val accountId: String,
    val playerCardUrl: String?,
    val gameName: String,
    val tagLine: String,
    val level: Int,
    val isActive: Boolean
)

@Singleton
class FakeRiotAccountRepository @Inject constructor(
    private val store: RiotAccountStore
) {
    // 현재 앱에서 관리 중인 계정 목록
    private val _accounts = MutableStateFlow<List<FakeRiotAccount>>(emptyList())
    val accounts: StateFlow<List<FakeRiotAccount>> = _accounts.asStateFlow()

    // DataStore에 저장된 계정 ID/활성 ID를 기반으로 메모리 계정 목록을 복원
    suspend fun restoreFromStore() {
        val ids = store.observeAccountIds().first()
            .filter { it.isNotBlank() }
            .distinct()
            .take(3)

        if (ids.isEmpty()) {
            _accounts.value = emptyList()
            return
        }

        val savedActiveId = store.observeActiveAccountId().first()
        val activeId = if (savedActiveId != null && ids.contains(savedActiveId)) {
            savedActiveId
        } else {
            // 저장된 active가 없거나 목록에 없으면 첫 계정을 활성으로 지정
            ids.first()
        }

        val restored = ids.map { id ->
            buildFakeAccount(id = id, isActive = (id == activeId))
        }

        _accounts.value = restored

        // 복원 결과를 DataStore에도 정리
        store.saveAccounts(
            accountIds = restored.map { it.accountId },
            activeAccountId = activeId
        )
    }

    // 계정 ID로부터 항상 동일한 가짜 계정 정보를 생성
    private fun buildFakeAccount(id: String, isActive: Boolean): FakeRiotAccount {
        val seed = id.hashCode()

        // id 기반으로 안정적인 값 생성
        val nickNum = ((seed % 900).let { if (it < 0) it * -1 else it }) + 100
        val level = ((seed % 301).let { if (it < 0) it * -1 else it }) + 50

        return FakeRiotAccount(
            accountId = id,
            playerCardUrl = null,
            gameName = "플레이어$nickNum",
            tagLine = "KR1",
            level = level,
            isActive = isActive
        )
    }

    // 토큰을 기반으로 새 계정을 추가하고 방금 추가한 계정을 활성 계정으로 설정
    suspend fun addAccount(token: String) {
        // token 기반으로 가짜 계정 생성
        val id = "acc_${token.hashCode()}"

        // 이미 같은 계정이 있으면 중복 추가 방지
        if (_accounts.value.any { it.accountId == id }) return

        // UI 테스트용 임시 계정 정보 생성
        val newAccount = FakeRiotAccount(
            accountId = id,
            playerCardUrl = null,
            gameName = "플레이어${Random.nextInt(100, 999)}",
            tagLine = "KR1",
            level = Random.nextInt(50, 350),
            isActive = true
        )

        // 기존 계정은 모두 비활성화하고, 새 계정을 추가한 뒤 최대 3개로 제한
        val updated = _accounts.value
            .map { it.copy(isActive = false) }
            .toMutableList()
            .apply { add(newAccount) }
            .take(3)

        // 메모리 상태 갱신
        _accounts.value = updated

        // DataStore 저장 갱신(계정 ID 목록 + 활성 계정 ID)
        store.saveAccounts(updated.map { it.accountId }, newAccount.accountId)
    }

    // 특정 계정을 활성 계정으로 전환
    suspend fun switchAccount(accountId: String) {
        // 선택된 계정만 활성화
        _accounts.value = _accounts.value.map {
            it.copy(isActive = it.accountId == accountId)
        }
        // DataStore 저장에도 활성 계정 반영
        store.saveAccounts(
            _accounts.value.map { it.accountId },
            accountId
        )
    }

    // 특정 계정을 제거
    suspend fun removeAccount(accountId: String) {
        val currentList = _accounts.value
        val removed = currentList.firstOrNull { it.accountId == accountId } ?: return
        val wasActive = removed.isActive

        // 메모리에서 삭제
        val remaining = currentList.filterNot { it.accountId == accountId }

        // 남은 계정이 없으면 전체 로그아웃 상태로 전환
        if (remaining.isEmpty()) {
            _accounts.value = emptyList()
            store.removeAccount(accountId)
            return
        }

        // 활성 계정 보장 - 활성 계정을 지웠다면 남은 첫 계정을 자동 활성화
        val newActiveId = if (wasActive) {
            remaining.first().accountId
        } else {
            // 비활성 계정을 지웠다면 기존 활성 계정 유지
            remaining.firstOrNull { it.isActive }?.accountId
                ?: remaining.first().accountId
        }

        // 새 활성 기준으로 목록 재구성
        val updated = remaining.map { it.copy(isActive = it.accountId == newActiveId) }

        // 메모리 상태 갱신
        _accounts.value = updated

        // DataStore 저장 동기화(계정 ID 목록 + 새 활성 계정 ID로 최종 덮어쓰기)
        store.saveAccounts(
            accountIds = updated.map { it.accountId },
            activeAccountId = newActiveId
        )
    }

    // 현재 활성 계정 관찰
    fun observeActiveAccount(): Flow<FakeRiotAccount?> =
        accounts.map { list -> list.firstOrNull { it.isActive } }

    // 저장된 모든 계정 정보 삭제
    suspend fun clearAll() {
        store.clearAll()
        _accounts.value = emptyList()
    }
}
