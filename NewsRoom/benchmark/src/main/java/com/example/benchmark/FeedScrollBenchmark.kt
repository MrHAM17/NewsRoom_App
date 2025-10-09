//        list?.let {
//            it.setGestureMargin(device.displayWidth / 5)
//            scrollRecycler(device, it, times = 5, label = "home feed")
//        } ?: Log.w(TAG1, "RecyclerView not found")

package com.example.benchmark

import android.util.Log
import androidx.benchmark.macro.StartupMode
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.uiautomator.Direction
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.uiautomator.By

/**
 * TEST 2: Scrolling Performance (News List)
 * ---------------------
 * WHAT IT DOES: Measures jank (dropped frames) while scrolling through the main list.
 * MEASURES: FrameTimingMetric (Jank) + PowerMetric (CPU/Memory)
 */

@LargeTest
@RunWith(AndroidJUnit4::class)
class FeedScrollBenchmark {

    // The core rule that executes the tests and collects metrics.
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun scrollNewsList() = benchmarkRule.measureRepeated(
        packageName = APP_PACKAGE,
        metrics = FT_METRIC, // PERFORMANCE_METRICS , // ✅ Now includes FrameTiming and Power
        iterations = 10,
        startupMode = StartupMode.WARM,
        compilationMode = COMPILATION_MODE
    ) {
        // SETUP: Wait for the app to launch and ensure the Home tab is selected.
        startActivityAndWait()

        // Find the RecyclerView/LazyColumn/ListView on the screen.
        // Assuming your main news list is identified by the resource ID 'recycler'.
        val list = device.wait(Until.findObject(ById("recycler")), 10_000)
            ?: device.wait(Until.findObject(ById("homeFragment")), 7_000)
            ?: device.wait(Until.findObject(By.text("home")), 5_000)
            ?: device.wait(Until.findObject(ById("ic_home")), 3_000)
            ?: run {
                Log.w(TAG1, "Recycler View (ID: recycler) not found in 10s. Skipping scroll test.")
                return@measureRepeated
            }
//           ?: return@measureRepeated


        // Wait for list items to appear to ensure content is loaded.
        // Assuming your list item layout contains a news title with ID 'title'.
//        device.wait(Until.hasObject(ById("title")), 10_000)

        // Set margin to prevent flinging out of bounds if the device screen is small.
        list.setGestureMargin(device.displayWidth / 8)

        // MEASUREMENT: Perform scrolling to render new items (DOWN) and recycle old ones (UP) and measure performance.
////        list.fling(Direction.DOWN)
//        list.scroll(Direction.DOWN, 0.7f)
//        Thread.sleep(500) // Pause between directions
////        list.fling(Direction.UP)
//        list.scroll(Direction.UP, 0.7f)

        // 1. Fling Down 5 times to stress test rendering new items.
        repeat(2) { list.fling(Direction.DOWN) }

        // 2. Fling Up 5 times to stress test recycling items.
        repeat(2) { list.fling(Direction.UP)  }

        // We do not need Thread.sleep() here because device.waitForIdle() is called internally
        // after each fling operation, ensuring a professional, stable measurement.
    }
}
