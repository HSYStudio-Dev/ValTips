package com.hsystudio.valtips.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "valtips_prefs")

@Singleton
class AppPrefsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val LAST_SYNC_TIMESTAMP = stringPreferencesKey("my_last_sync_timestamp")

        // 사용자가 동의한 약관 버전
        val ACCEPTED_TERMS_VERSION = stringPreferencesKey("accepted_terms_version")

        // 사용자가 동의한 개인정보처리방침 버전
        val ACCEPTED_PRIVACY_VERSION = stringPreferencesKey("accepted_privacy_version")

        // 프로 멤버십 여부
        val IS_PRO_MEMBER = booleanPreferencesKey("is_pro_member")
    }

    // ─────────────────────────────
    // 조회
    // ─────────────────────────────
    val onboardingCompletedFlow: Flow<Boolean> =
        context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { it[PreferencesKeys.ONBOARDING_COMPLETED] ?: false }

    val lastSyncFlow: Flow<String?> =
        context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { it[PreferencesKeys.LAST_SYNC_TIMESTAMP] }

    // 동의한 약관 버전 조회
    val acceptedTermsVersionFlow: Flow<String?> =
        context.dataStore.data
            .catch { e ->
                if (e is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw e
                }
            }
            .map { it[PreferencesKeys.ACCEPTED_TERMS_VERSION] }

    // 동의한 개인정보처리방침 버전 조회
    val acceptedPrivacyVersionFlow: Flow<String?> =
        context.dataStore.data
            .catch { e ->
                if (e is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw e
                }
            }
            .map { it[PreferencesKeys.ACCEPTED_PRIVACY_VERSION] }

    // 프로 멤버십 상태 조회
    val isProMemberFlow: Flow<Boolean> =
        context.dataStore.data
            .catch { e ->
                if (e is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw e
                }
            }
            .map { it[PreferencesKeys.IS_PRO_MEMBER] ?: false }

    // ─────────────────────────────
    // 저장
    // ─────────────────────────────
    suspend fun setOnboardingCompleted(done: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.ONBOARDING_COMPLETED] = done }
    }

    suspend fun setLastSync(timestamp: String) {
        context.dataStore.edit { it[PreferencesKeys.LAST_SYNC_TIMESTAMP] = timestamp }
    }

    // 약관/개인정보처리방침 동의 버전 저장
    suspend fun setAcceptedPolicyVersions(
        termsVersion: String,
        privacyVersion: String
    ) {
        context.dataStore.edit {
            it[PreferencesKeys.ACCEPTED_TERMS_VERSION] = termsVersion
            it[PreferencesKeys.ACCEPTED_PRIVACY_VERSION] = privacyVersion
        }
    }

    // 프로 멤버십 상태 저장
    suspend fun setProMember(isPro: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.IS_PRO_MEMBER] = isPro }
    }

    // ─────────────────────────────
    // 삭제
    // ─────────────────────────────
    suspend fun clearOnboarding() {
        context.dataStore.edit { it.remove(PreferencesKeys.ONBOARDING_COMPLETED) }
    }

    suspend fun clearLastSync() {
        context.dataStore.edit { it.remove(PreferencesKeys.LAST_SYNC_TIMESTAMP) }
    }

    suspend fun clearAcceptedPolicyVersions() {
        context.dataStore.edit {
            it.remove(PreferencesKeys.ACCEPTED_TERMS_VERSION)
            it.remove(PreferencesKeys.ACCEPTED_PRIVACY_VERSION)
        }
    }
}
