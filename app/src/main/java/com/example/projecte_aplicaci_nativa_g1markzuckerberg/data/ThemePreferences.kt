package com.example.projecte_aplicaci_nativa_g1markzuckerberg.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 1️⃣ Define tu DataStore de forma global sobre Context
private val Context.dataStore by preferencesDataStore("settings")

class ThemePreferences(private val context: Context) {
    companion object {
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    }

    // Flow que emite true si está en oscuro
    val isDarkModeFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[DARK_MODE_KEY] ?: false }

    // Llama a esto para guardar la preferencia
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = enabled
        }
    }
}
