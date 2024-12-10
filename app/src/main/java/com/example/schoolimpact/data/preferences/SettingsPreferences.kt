package com.example.schoolimpact.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.SettingsPreferences by preferencesDataStore(name = "settings")

@Singleton
class SettingsPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val settingsDataStore = context.SettingsPreferences
    private val themeKey = booleanPreferencesKey("theme_setting")

    fun getThemeSetting(): Flow<Boolean> {
        return settingsDataStore.data.map { preferences ->
            preferences[themeKey] ?: false
        }
    }

    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        settingsDataStore.edit { preferences ->
            preferences[themeKey] = isDarkModeActive
        }
    }

}