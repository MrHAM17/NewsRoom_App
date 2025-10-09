//package com.example.benchmark.baselineprofile
//
//import androidx.benchmark.macro.junit4.BaselineProfileRule
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import androidx.test.uiautomator.By
//import com.example.benchmark.APP_PACKAGE
//import com.example.benchmark.ById
//import com.example.benchmark.launchApp
//import com.example.benchmark.scrollRecycler
//import androidx.test.uiautomator.Until
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//
//@RunWith(AndroidJUnit4::class)
//class BaselineProfileGenerator {
//
//    @get:Rule
//    val baselineProfileRule = BaselineProfileRule()
//
//    @Test
//    fun generateBaselineProfile() = baselineProfileRule.collect(packageName = APP_PACKAGE) {
//
//        // ---------- App Startup ----------
//        launchApp(waitForRecycler = true, id = "recycler")
//        device.waitForIdle()
//        Thread.sleep(3000) // Wait for feed to fully load
//
//        // ---------- Iterate All Home Tabs ----------
//        val homeTabs = listOf("General", "Business", "Entertainment", "Health", "Science", "Sports", "Technology")
//        homeTabs.forEach { tabText ->
//            device.findObject(By.text(tabText))?.click()
//            device.wait(Until.hasObject(ById("recycler")), 5_000)
//            Thread.sleep(1000)
//
//            val recycler = device.findObject(ById("recycler"))
//            recycler?.let {
//                // Scroll the feed a few times to warm up UI
//                scrollRecycler(device, it, times = 3, label = "$tabText feed")
//
//                // Open first 2 articles (or as many as available)
//                it.children.take(2).forEach { article ->
//                    article.click()
//                    device.waitForIdle()
//                    Thread.sleep(1500)
//
//                    // Interact inside article
//                    device.findObject(By.text("ADD BOOKMARK"))?.click()
//                    Thread.sleep(500)
//
//                    device.findObject(By.text("SHARE"))?.click()
//                    device.pressBack()
//                    device.findObject(By.text("OPEN IN BROWSER.."))?.click()
//                    device.pressBack()
//
//                    device.pressBack() // Back to tab feed
//                    device.waitForIdle()
//                }
//            }
//        }
//
//        // ---------- Navigate to Saved/Bookmarks ----------
//        device.findObject(By.text("Saved"))?.click()
//        device.wait(Until.hasObject(ById("rvBookmarks")), 5_000)
//        Thread.sleep(2000)
//
//        val savedRecycler = device.findObject(ById("rvBookmarks"))
//        savedRecycler?.let { scrollRecycler(device, it, times = 2, label = "saved list") }
//
//        // ---------- Navigate to Search ----------
//        device.findObject(By.text("Search"))?.click()
//        device.waitForIdle()
//        val searchBox = device.findObject(By.res(APP_PACKAGE, "query"))
//        searchBox?.setText("android")
//        device.waitForIdle()
//        Thread.sleep(2000)
//
//        val searchRecycler = device.wait(Until.findObject(ById("recycler")), 5_000)
//        searchRecycler?.let { scrollRecycler(device, it, times = 2, label = "search results") }
//
//        // Open first search result
//        searchRecycler?.children?.firstOrNull()?.click()
//        device.waitForIdle()
//        Thread.sleep(2000)
//
//        device.findObject(By.text("ADD BOOKMARK"))?.click()
//        Thread.sleep(500)
//        device.pressBack()
//
//        // ---------- Navigate to Sources (after Search) ----------
//        device.findObject(By.text("Sources"))?.click()
//        Thread.sleep(1000)
//        device.pressBack()
//
//        // ---------- Navigate to Settings ----------
//        device.findObject(By.text("Settings"))?.click()
//        device.waitForIdle()
//        Thread.sleep(1000)
//
//        // Switch themes for baseline coverage
//        // Switch to Dark theme
//        device.findObject(By.text("Dark"))?.click()
//        Thread.sleep(500)
//
//        // ---------- User revisits after theme change ----------
//        // Home
//        device.findObject(By.text("Home"))?.click()
//        device.wait(Until.hasObject(ById("recycler")), 5_000)
//        device.findObject(ById("recycler"))?.children?.firstOrNull()?.click()
//        Thread.sleep(1500)
//        device.pressBack()
//
//        // Saved
//        device.findObject(By.text("Saved"))?.click()
//        Thread.sleep(1000)
//        device.findObject(ById("rvBookmarks"))?.children?.firstOrNull()?.click()
//        Thread.sleep(1000)
//        device.pressBack()
//
//        // Sources
//        device.findObject(By.text("Sources"))?.click()
//        Thread.sleep(1000)
//        device.pressBack()
//
//        // Back to Settings
//        device.findObject(By.text("Settings"))?.click()
//        device.waitForIdle()
//        Thread.sleep(1000)
//
//        // Switch to Light & Default
//        device.findObject(By.text("Light"))?.click()
//        Thread.sleep(500)
//        device.findObject(By.text("System Default"))?.click()
//        Thread.sleep(500)
//
//        // ---------- Export CSV ----------
//        device.waitForIdle()
//        device.findObject(By.text("Export"))?.click()
//        Thread.sleep(1000)  // wait for export dialog
//        device.pressBack()   // close dialog
//
//        // ---------- Import CSV ----------
//        device.findObject(By.text("Import"))?.click()
//        Thread.sleep(1000)  // wait for file picker (you may just press back)
//        device.pressBack()
//
//        device.pressHome()
//    }
//}

/*
   Above version will not generate the "OPTIMIZED" baseline profile even though it is correct.
*/


package com.example.benchmark.baselineprofile
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.Until
import com.example.benchmark.APP_PACKAGE
import com.example.benchmark.ById
import com.example.benchmark.LONG_WAIT
import com.example.benchmark.MEDIUM_WAIT
import com.example.benchmark.SHORT_WAIT
import com.example.benchmark.coreTabs
import com.example.benchmark.launchApp
import com.example.benchmark.scrollRecycler
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * FINAL PROFESSIONAL Baseline Profile Generator for NewsRoom.
 *
 * This version
 * * traces the application's most critical user journeys and
 * * uses centralized wait constants and state-aware waiting for maximum reliability
 * to generate a profile that optimizes app startup and run-time performance.
 *
 *
 * Flows covered:
 * 1. App Startup
 * 2. Tab Switching (UI/Data load)
 * 3. Feed Scrolling (RecyclerView inflation)
 * 4. Article Detail View (Screen transition)
 * 5. Database Write (Bookmarking)
 * 6. Navigation to Saved/Bookmarks
 * 7. Search (Input, Network, Results list)
 * 8. Theme Switching (Style application performance)
 */

@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {

    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generateBaselineProfile() = baselineProfileRule.collect(APP_PACKAGE,1 ) // 2 // 3 // instead of 10
    {

        // --- Local helpers (Correctly typed with BySelector) ---
        fun find(by: BySelector, timeoutMs: Long = MEDIUM_WAIT) = device.wait(Until.findObject(by), timeoutMs)
        fun clickIfFound(by: BySelector, timeoutMs: Long = MEDIUM_WAIT) = find(by, timeoutMs)?.click()
        fun waitForRecycler(rvId: String, timeoutMs: Long = LONG_WAIT) = device.wait(Until.hasObject(ById(rvId)), timeoutMs)

        // --- 1) App Startup (Cold Launch) ---
        launchApp(waitForRecycler = true, id = "recycler")
        device.waitForIdle()
        Thread.sleep(SHORT_WAIT)

        // --- 2) Feed Interactions (Tab switching, Scrolling, Detail View) ---
        coreTabs.forEach { tab ->
            clickIfFound(By.text(tab))
//            waitForRecycler()
            device.waitForIdle()

            val recycler = find(ById("recycler"), LONG_WAIT) ?: return@forEach
            recycler.let {
                // Scroll twice to capture inflation of list items
                scrollRecycler(device, it,2)    // label = "$tab feed"

                // Open first article (Screen transition performance)
                it.children.firstOrNull()?.click()
                device.waitForIdle()

                // Interact inside article (Database/UI sync performance)
                clickIfFound(By.text("ADD BOOKMARK"))
                Thread.sleep(SHORT_WAIT) // Pause for DB/UI action to trace

                device.pressBack()
        //                device.waitForIdle()
            }
        }

        // --- 3) Saved / Bookmarks (A different list implementation) ---
//        clickIfFound(By.text("Saved"))
//        clickIfFound(ById("savedFragment"),LONG_WAIT)
        clickIfFound(ById("savedFragment"))
//        device.wait(Until.hasObject(ById("savedFragment")), SHORT_WAIT) // LONG_WAIT // confirm fragment
        find(ById("rvBookmarks")) ?.let { scrollRecycler(device, it,1) } // label = "saved list"
//        device.pressBack()
        device.waitForIdle()

        // --- 4) Search (Typing, network request, result list) ---
        clickIfFound(ById("searchFragment"))
        var searchBox = find(ById("query"))
        searchBox?.setText("android")
        // Wait for the new recycler (search results) to appear
        waitForRecycler("recycler")
        find(ById("recycler"),SHORT_WAIT) ?.let { scrollRecycler(device, it,2) }
        device.pressBack()
        device.waitForIdle()

        // --- 5) Sources (Network request, result list) ---
        clickIfFound(ById("sourcesFragment"))
        waitForRecycler("recycler")
        find(ById("recycler"),SHORT_WAIT) ?.let { scrollRecycler(device, it,1) }
        device.waitForIdle()

        // --- 6) Settings & Theme Switching (Style application performance) ---
        clickIfFound(ById("settingsFragment"))
        // Trigger all theme changes sequentially
        clickIfFound(By.text("Light"))
        Thread.sleep(SHORT_WAIT)
        clickIfFound(By.text("System Default"))
        Thread.sleep(SHORT_WAIT)
        clickIfFound(By.text("Dark"))
        Thread.sleep(SHORT_WAIT)
        device.pressBack()

        // Final: return to profile the final theme application and UI inflation
        find(ById("recycler")) ?.let { scrollRecycler(device, it,2) }
        clickIfFound(ById("savedFragment"))
        find(ById("rvBookmarks")) ?.let { scrollRecycler(device, it,1) }
        clickIfFound(ById("searchFragment"))
        searchBox = find(ById("query"))
        searchBox?.setText("android")
        find(ById("recycler"),LONG_WAIT) ?.let { scrollRecycler(device, it,2) }
        clickIfFound(ById("sourcesFragment"))
        find(ById("recycler"),SHORT_WAIT) ?.let { scrollRecycler(device, it,1) }
        clickIfFound(ById("settingsFragment"))
    }
}
