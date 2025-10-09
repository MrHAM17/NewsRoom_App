package com.example.newsroom.core.theme

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ThemeManagerTest {

    private lateinit var context: Context
    private lateinit var manager: ThemeManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        manager = ThemeManager(context)
    }

    @Test
    fun saveAndLoadThemePreference() = runBlocking {
        // Save DARK theme
        manager.setTheme(ThemeMode.DARK)

        // Read theme from flow
        val saved = manager.themeFlow.first()

        // Verify
        assertEquals(ThemeMode.DARK, saved)
    }
}
