package com.example.newsroom.core.theme

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Create DataStore at app level
private val Context.dataStore by preferencesDataStore("settings")

@Singleton
class ThemeManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val THEME = stringPreferencesKey("theme_mode")
    }

    val themeFlow: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        val value = prefs[Keys.THEME] ?: ThemeMode.SYSTEM.name
        ThemeMode.valueOf(value)
    }

    suspend fun setTheme(mode: ThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[Keys.THEME] = mode.name
        }
    }
}
