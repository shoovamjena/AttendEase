package com.example.attendease

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val THEME_COLOR_KEY = intPreferencesKey("theme_color")
    }

    // Function to save name
    suspend fun saveUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = name
        }
    }

    // Function to retrieve name
    val getUserName: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[USER_NAME_KEY] }

    // Save theme color
    suspend fun saveThemeColor(color: Int) {
        context.dataStore.edit { preferences ->
            preferences[THEME_COLOR_KEY] = color
        }
    }

    // Retrieve theme color
    suspend fun getThemeColor(): Int? {
        return context.dataStore.data.map { preferences ->
            preferences[THEME_COLOR_KEY]
        }.firstOrNull()
    }
}