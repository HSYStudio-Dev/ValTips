package com.hsystudio.valtips.data.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey

import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "riot_account_store"
)

@Singleton
class RiotAccountStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private companion object {
        // 현재 활성화된 라이엇 계정 ID를 저장하는 키
        val ACTIVE_ACCOUNT_ID_KEY = stringPreferencesKey("active_account_id")
        // 연결된 라이엇 계정 ID 목록을 저장하는 키
        val ACCOUNT_IDS_KEY = stringPreferencesKey("account_ids")
    }

    // 현재 활성 계정 ID를 Flow로 관찰
    fun observeActiveAccountId(): Flow<String?> =
        context.dataStore.data.map { it[ACTIVE_ACCOUNT_ID_KEY] }

    // 저장된 계정 ID 목록을 Flow로 관찰
    fun observeAccountIds(): Flow<List<String>> =
        context.dataStore.data.map {
            it[ACCOUNT_IDS_KEY]
                ?.split(",")
                ?.filter { id -> id.isNotBlank() }
                ?: emptyList()
        }

    // 계정 목록과 활성 계정을 한 번에 저장
    suspend fun saveAccounts(accountIds: List<String>, activeAccountId: String?) {
        context.dataStore.edit {
            // 계정 ID 목록을 콤마 문자열로 저장
            it[ACCOUNT_IDS_KEY] = accountIds.joinToString(",")

            // 활성 계정이 있으면 해당 ID 저장
            activeAccountId?.let { id ->
                it[ACTIVE_ACCOUNT_ID_KEY] = id
            }
        }
    }

    // 특정 계정을 저장소에서 제거
    suspend fun removeAccount(accountId: String) {
        context.dataStore.edit {
            // 기존 계정 목록에서 제거 대상 계정 ID 제외
            val current = it[ACCOUNT_IDS_KEY]
                ?.split(",")
                ?.filterNot { id -> id == accountId }
                ?: emptyList()

            // 갱신된 계정 목록 다시 저장
            it[ACCOUNT_IDS_KEY] = current.joinToString(",")

            // 제거한 계정이 활성 계정이면 활성 계정 정보도 삭제
            if (it[ACTIVE_ACCOUNT_ID_KEY] == accountId) {
                it.remove(ACTIVE_ACCOUNT_ID_KEY)
            }
        }
    }
}
