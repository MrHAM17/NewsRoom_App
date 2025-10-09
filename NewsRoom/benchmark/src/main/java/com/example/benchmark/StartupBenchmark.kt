package com.example.benchmark

import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * TEST 1: App Startup
 * ---------------------
 * MEASURES: StartupTimingMetric (Time)
 * NOTE: We only use ST_METRIC here because PowerMetric is less relevant for a quick launch.
 */


@LargeTest
@RunWith(AndroidJUnit4::class)
class StartupBenchmark {

    // The core rule that executes the tests and collects metrics.
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    private fun runStartup(mode: StartupMode) {
        benchmarkRule.measureRepeated(
            packageName = APP_PACKAGE,
            // Using only StartupTimingMetric for cleaner launch results
            metrics = ST_METRIC,
            iterations = 5,
            startupMode = mode,
            compilationMode = COMPILATION_MODE
        ) {
            pressHome()
            startActivityAndWait()
        }
    }

    @Test
    fun startupCold() = runStartup(StartupMode.COLD)
    @Test
    fun startupWarm() = runStartup(StartupMode.WARM)
    @Test
    fun startupHot() = runStartup(StartupMode.HOT)

}

