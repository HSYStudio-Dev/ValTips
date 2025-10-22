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
    }

    // 조회
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

    // 저장
    suspend fun setOnboardingCompleted(done: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.ONBOARDING_COMPLETED] = done }
    }

    suspend fun setLastSync(timestamp: String) {
        context.dataStore.edit { it[PreferencesKeys.LAST_SYNC_TIMESTAMP] = timestamp }
    }

    // 삭제
    suspend fun clearOnboarding() {
        context.dataStore.edit { it.remove(PreferencesKeys.ONBOARDING_COMPLETED) }
    }

    suspend fun clearLastSync() {
        context.dataStore.edit { it.remove(PreferencesKeys.LAST_SYNC_TIMESTAMP) }
    }
}
