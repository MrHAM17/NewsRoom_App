//package com.example.benchmark
//
//import android.util.Log
//import androidx.benchmark.macro.junit4.MacrobenchmarkRule
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import androidx.test.filters.LargeTest
//import androidx.test.platform.app.InstrumentationRegistry
//import androidx.test.uiautomator.*
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//
//private const val BOOKMARK_COUNT = 3 // Top N articles to bookmark
//
//@RunWith(AndroidJUnit4::class)
//@LargeTest
//class SavedBenchmark {
//
//    @get:Rule
//    val benchmarkRule = MacrobenchmarkRule()
//
//    private fun setupBookmarks(device: UiDevice) {
//
//        // Helper to always get a fresh recycler
//        fun findHomeRecycler(): UiObject2? { return device.wait(Until.findObject(ById("recycler")), 5_000)  }
//
//        // Click Home tab first
//        val homeTab: UiObject2? = device.wait(Until.findObject(ById("homeFragment")), 10_000)
//            ?: device.wait(Until.findObject(By.text("home")), 3_000)
//            ?: device.wait(Until.findObject(By.desc("ic_home")), 3_000)
//
//        homeTab?.click()
//        device.waitForIdle()
//
//        val homeRecycler = device.wait(Until.findObject(ById("recycler")), 10_000)
//        if (homeRecycler == null) {
//            Log.w(TAG3, "Home recycler not found. Cannot bookmark.")
//            return
//        }
//
//        for (i in 0 until BOOKMARK_COUNT) {
//
//            val homeRecycler = findHomeRecycler()
//            if (homeRecycler == null) {
//                Log.w(TAG3, "Home recycler not found, stopping loop.")
//                break
//            }
//
//            val item = homeRecycler.children.getOrNull(i) ?: break
//            item.click()
//            device.waitForIdle()
//
//            var bookmarkButton = device.wait(Until.findObject(ById("bookmarkButton")), 3_000)
//
//            if (bookmarkButton == null) {
//                // Swipe slightly if button is offscreen
//                device.swipe(
//                    device.displayWidth / 2,
//                    (device.displayHeight * 0.8).toInt(),
//                    device.displayWidth / 2,
//                    (device.displayHeight * 0.3).toInt(),
//                    15
//                )
//                device.waitForIdle()
//                bookmarkButton = device.findObject(ById("bookmarkButton"))
//            }
//
//            bookmarkButton?.click()
//            Log.i(TAG3, "Setup bookmarked item index=$i")
//
//            device.pressBack()
//            device.waitForIdle()
//        }
//    }
//
//    @Before
//    fun prepareSavedArticles() {
//        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
//        device.pressHome()
//        device.waitForIdle()
//
//        // Launch the app fresh
//        device.executeShellCommand("am start -W $APP_PACKAGE/.MainActivity")
//        device.waitForIdle()
//
//        // Bookmark top articles in Home
//        setupBookmarks(device)
//    }
//
//    @Test
//    fun openSavedArticles() = benchmarkRule.measureRepeated(
//        packageName = APP_PACKAGE,
//        metrics = FT_METRIC,
//        compilationMode = COMPILATION_MODE,
//        iterations = 3
//    ) {
//        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
//
//        // Navigate to Saved tab safely using multiple fallbacks
//        val savedTab: UiObject2? = device.wait(Until.findObject(ById("savedFragment")), 10_000)
//            ?: device.wait(Until.findObject(By.text("Saved")), 5_000)
//            ?: device.wait(Until.findObject(By.desc("ic_bookmarks")), 3_000)
//
//        requireNotNull(savedTab) { "Saved fragment not found" }
//        savedTab.click()
//        device.waitForIdle()
//        Log.i(TAG3, "Saved fragment found.")
//
//
//        // Wait for saved RecyclerView
//        val savedRecycler = device.wait(Until.findObject(ById("rvBookmarks")), 5_000)
//        requireNotNull(savedRecycler) { "rvBookmarks not found" }
//
//        // Scroll down once
//        savedRecycler.scroll(Direction.DOWN, 0.7f)
//        device.waitForIdle()
//
//        // Optional small wait to guarantee frame samples
//        device.waitForIdle(1_000)
//    }
//}
