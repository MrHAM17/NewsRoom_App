// @file:OptIn(ExperimentalMetricApi::class)

package com.example.benchmark

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
// import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.StartupTimingMetric
import androidx.test.uiautomator.*
// import androidx.benchmark.macro.PowerMetric

/**
 * * CORE CONFIGURATION
 *  1. APP_PACKAGE: MUST include the ".benchmark" suffix!
 *  2. COMPILATION_MODE: Set to BaselineProfile() for measuring final, optimized performance.
 * */

const val APP_PACKAGE = "com.example.newsroom.benchmark"
val coreTabs = listOf("General", "Technology", "Health")
//val coreTabs = listOf("General", "Business", "Entertainment", "Health", "Science", "Sports", "Technology")

val FT_METRIC = listOf(FrameTimingMetric())
val ST_METRIC = listOf(StartupTimingMetric())
val COMPILATION_MODE = CompilationMode.Partial( baselineProfileMode = BaselineProfileMode.UseIfAvailable )
//val PERFORMANCE_METRICS = listOf(
//    FrameTimingMetric(),       // Measures UI smoothness (Frames/Jank)
//    StartupTimingMetric(),     // Measures App Launch Speed (Startup/Time)
//    PowerMetric(PowerMetric.Energy()),     // Measures CPU/Memory/Power usage
//    PowerMetric( PowerMetric.Power()),     // Measures CPU/Memory/Power usage
//    PowerMetric(PowerMetric.Battery())     // Measures CPU/Memory/Power usage
//)
const val TAG1 = "Benchmark_FeedScroll"
const val TAG2 = "Benchmark_Search"
const val TAG3 = "Benchmark_Saved"

// --- Configurable Wait Constants (Centralized and easily adjustable) ---
val SHORT_WAIT = 1_000L    // General stabilization pause (after complex action)
val MEDIUM_WAIT = 3_000L   // Standard element find timeout
val LONG_WAIT = 5_000L     // Timeout for major layout changes (Recycler View load)


// ------------------- Helper Functions -------------------

/** Creates a BySelector based on resource ID within the target app package. */
fun ById (id: String) = By.res(APP_PACKAGE, id)

/** Launches the app from the home screen and waits for the initial recycler element. */
fun MacrobenchmarkScope.launchApp(waitForRecycler: Boolean = true, id: String? = null) {
    pressHome()
    startActivityAndWait()
    device.waitForIdle()
    if (waitForRecycler && id != null) { device.wait(Until.hasObject(ById(id)), 10_000)  }
}

/** Scrolls a RecyclerView a specified number of times to profile item inflation and binding. */
fun scrollRecycler( device: UiDevice,  recycler: UiObject2, times: Int
//    ,label: String = "recycler"
) {
    repeat(times) { i ->
//        Log.d("Benchmark_Scroll", "Scrolling $label [$i]")
        recycler.scroll(Direction.DOWN, 0.7f)
        Thread.sleep(500) // Small pause between scrolls for trace clarity
//        device.waitForIdle()
    }
}
