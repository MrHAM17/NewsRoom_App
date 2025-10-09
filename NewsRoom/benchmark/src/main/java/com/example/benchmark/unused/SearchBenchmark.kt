//package com.example.benchmark
//
//import android.util.Log
//import androidx.benchmark.macro.junit4.MacrobenchmarkRule
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import androidx.test.filters.LargeTest
//import androidx.test.platform.app.InstrumentationRegistry
//import androidx.test.uiautomator.*
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//
//@LargeTest
//@RunWith(AndroidJUnit4::class)
//class SearchBenchmark {
//
//    @get:Rule
//    val benchmarkRule = MacrobenchmarkRule()
//
//    @Test
//    fun searchAndOpenArticle() = benchmarkRule.measureRepeated(
//        packageName = APP_PACKAGE,
//        metrics = FT_METRIC,
//        compilationMode = COMPILATION_MODE,
//        iterations = 3
//    ) {
//        launchApp(false, "recycler")
//        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
//
//        // Open Search tab
//        val searchTab = device.wait(Until.findObject(ById("searchFragment")), 10_000)
//            ?: device.wait(Until.findObject(By.text("Search")), 5_000)
//        requireNotNull(searchTab) { "Search tab not found" }
//        searchTab.click()
//        device.waitForIdle()
//
//        // Find search box
//        val searchBox = device.wait(Until.findObject(ById("query")), 3_000)
//            ?: device.wait(Until.findObject(By.text("Search articles...")), 3_000)
//            ?: device.wait(Until.findObject(By.desc("ic_search")), 3_000)
//        requireNotNull(searchBox) { "Search box not found" }
//
//        // Type keyword
//        searchBox.text = "" // clear existing text
//        searchBox.setText("iphone")
//        device.waitForIdle(2_000)
//
//        // Get results
//        val recycler = device.wait(Until.findObject(ById("recycler")), 10_000)
//        requireNotNull(recycler) { "Recycler not found for search results" }
//
//        scrollRecycler(device, recycler, times = 2, label = "search results")
//
//        if (recycler.childCount > 0) {
//            recycler.children[0].click()
//            device.waitForIdle()
//
//            // --- Open in browser ---
//            var open = device.findObject(ById("openButton"))
//            if (open == null) {
//                device.swipe(
//                    device.displayWidth / 2,
//                    (device.displayHeight * 0.8).toInt(),
//                    device.displayWidth / 2,
//                    (device.displayHeight * 0.3).toInt(),
//                    15
//                )
//                device.waitForIdle()
//                open = device.findObject(ById("openButton"))
//            }
//            open?.apply {
//                click()
//                device.waitForIdle(3_000)
//                // Ensure we come back from browser
//                repeat(1) { device.pressBack(); device.waitForIdle(500) }
//                Log.i(TAG2, "Open in browser clicked + returned")
//            }
////            Log.i(TAG2, "Open in browser clicked + returned")
//
//            // --- Bookmark button ---
//            var bookmark = device.findObject(ById("bookmarkButton"))
//            if (bookmark == null) {
//                device.swipe(
//                    device.displayWidth / 2,
//                    (device.displayHeight * 0.8).toInt(),
//                    device.displayWidth / 2,
//                    (device.displayHeight * 0.3).toInt(),
//                    15
//                )
//                device.waitForIdle()
//                bookmark = device.findObject(ById("bookmarkButton"))
//            }
//            bookmark?.click()
//            if (bookmark?.isEnabled!!) { Log.i(TAG2, "Bookmark clicked") }
//            else { Log.i(TAG2, "Bookmark already clicked") }
//
//            // --- Share button ---
//            var share = device.findObject(ById("shareButton"))
//            if (share == null) {
//                device.swipe(
//                    device.displayWidth / 2,
//                    (device.displayHeight * 0.8).toInt(),
//                    device.displayWidth / 2,
//                    (device.displayHeight * 0.3).toInt(),
//                    15
//                )
//                device.waitForIdle()
//                share = device.findObject(ById("shareButton"))
//            }
//            share?.apply {
//                click()
//                device.waitForIdle(3_000)
//                device.pressBack()
//                Log.i(TAG2, "Share clicked & get back")
//            }
////            Log.i(TAG2, "Share clicked")
//        }
//    }
//}
