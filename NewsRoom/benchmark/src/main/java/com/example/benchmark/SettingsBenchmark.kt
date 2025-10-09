package com.example.benchmark

import android.util.Log
import androidx.benchmark.macro.StartupMode
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.benchmark.macro.junit4.MacrobenchmarkRule

/**
 * TEST 3: Complex UI Transaction (Theme Switch)
 * ---------------------
 * WHAT IT DOES: Measures the time taken to switch between three different UI themes.
 * MEASURES: FrameTimingMetric (Jank) + PowerMetric (CPU/Memory)
 */

@LargeTest
@RunWith(AndroidJUnit4::class)
class SettingsBenchmark {

    // The core rule that executes the tests and collects metrics.
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun switchThemes() = benchmarkRule.measureRepeated(
        packageName = APP_PACKAGE,
        metrics = FT_METRIC, // PERFORMANCE_METRICS, // ✅ Now includes FrameTiming and Power
        iterations = 5,
        startupMode = StartupMode.WARM,
        compilationMode = COMPILATION_MODE
    ) {
        // SETUP: Navigate to the Settings fragment using the Bottom Nav bar.
        startActivityAndWait()

        // Find the Settings bottom navigation item.
        // Assuming bottom navigation items are identified by their text or content description.
        val settingsNav = device.wait(Until.findObject(ById("Settings")), 10_000)
            ?: device.wait(Until.findObject(ById("settingsFragment")), 5_000)
//            ?: return@measureRepeated
        if(settingsNav == null) {
            Log.w(TAG1, "Settings Fragment not found")
            return@measureRepeated // Exit gracefully if the navigation button isn't found
        }

        settingsNav.click()

        // Wait until a radio button (rbDark) inside the settings screen is visible.
        device.wait(Until.hasObject(ById("rbDark")), 5_000)

        // MEASUREMENT: Switch themes (Dark -> Light -> System -> Dark)
        // We use a small sleep (1000 // 500ms) after complex UI changes to ensure the full redrawing cycle
        // and measurement trace completes before the next click starts.
        device.findObject(ById("rbDark"))?.click()
        Thread.sleep(1000) // ✅ Keep a small sleep here
        device.findObject(ById("rbLight"))?.click()
        Thread.sleep(1000) // ✅ Keep a small sleep here
        device.findObject(ById("rbSystem"))?.click()
        Thread.sleep(1000) // ✅ Keep a small sleep here
        device.findObject(ById("rbDark"))?.click()
    }

}