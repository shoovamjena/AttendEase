package com.example.attendease

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.attendease.ui.theme.ThemePreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val THEME_COLOR_KEY = intPreferencesKey("theme_color")
        private val TARGET_KEY = floatPreferencesKey("target")
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
    }


    suspend fun saveUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = name
        }
    }


    val getUserName: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[USER_NAME_KEY] }

    suspend fun saveThemeColor(color: Int) {
        context.dataStore.edit { preferences ->
            preferences[THEME_COLOR_KEY] = color
        }
    }

    suspend fun getThemeColor(): Int? {
        return context.dataStore.data.map { preferences ->
            preferences[THEME_COLOR_KEY]
        }.firstOrNull()
    }

    suspend fun saveTarget(context: Context, target: Float){
        context.dataStore.edit { preferences ->
            preferences[TARGET_KEY] = target
        }
    }

    val targetFlow: Flow<Float> = context.dataStore.data
        .map { preferences ->
            preferences[TARGET_KEY] ?: 75f
    }

    suspend fun saveThemePreference(mode: ThemePreference) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode.name
        }
    }

    val themePreferenceFlow: Flow<ThemePreference> = context.dataStore.data
        .map { preferences ->
            val mode = preferences[THEME_MODE_KEY]
            ThemePreference.values().firstOrNull { it.name == mode } ?: ThemePreference.SYSTEM_DEFAULT
        }
}