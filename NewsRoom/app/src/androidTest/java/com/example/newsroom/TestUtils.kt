package com.example.newsroom

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.hamcrest.Matcher
import android.os.Build
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import android.provider.Settings
import android.content.ContentResolver
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import org.hamcrest.Matchers.allOf
import org.junit.rules.ExternalResource
import androidx.test.platform.app.InstrumentationRegistry

object TestUtils {

    /** Espresso helper to wait for a given number of milliseconds */
    fun waitFor(millis: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()

            override fun getDescription(): String = "Wait for $millis milliseconds."

            override fun perform(uiController: UiController, view: View) {
                uiController.loopMainThreadForAtLeast(millis)
            }
        }
    }

    fun waitForRecyclerViewItemCount(
        recyclerViewId: Int,
        minItemCount: Int,
        timeout: Long = 5000L
    ) {
        val startTime = System.currentTimeMillis()
        do {
            var itemCount = 0
            try {
                onView(withId(recyclerViewId)).check { view, _ ->
                    val rv = view as androidx.recyclerview.widget.RecyclerView
                    itemCount = rv.adapter?.itemCount ?: 0
                }
            } catch (_: Exception) {}
            if (itemCount >= minItemCount) return
            Thread.sleep(200)
        } while (System.currentTimeMillis() - startTime < timeout)
        // Final assertion
        onView(withId(recyclerViewId)).check(matches(hasMinimumChildCount(minItemCount)))
    }

    fun waitForSearchRecyclerViewItemCount(recyclerViewId: Int, expectedCount: Int, timeout: Long) {
        val start = System.currentTimeMillis()
        do {
            try {
                onView(withId(recyclerViewId))
                    .check(matches(allOf(
                        isDisplayed(),
                        hasMinimumChildCount(expectedCount)
                    )))
                return // success
            } catch (e: Exception) {
                // ignore, retry
            }
            Thread.sleep(200)
        } while (System.currentTimeMillis() - start < timeout)

        throw AssertionError("RecyclerView never reached $expectedCount items within $timeout ms")
    }
    //fun waitForSearchRecyclerViewItemCount(
    //    recyclerViewId: Int,
    //    minCount: Int,
    //    timeout: Long
    //): ViewInteraction {
    //    val startTime = System.currentTimeMillis()
    //    val endTime = startTime + timeout
    //
    //    do {
    //        try {
    //            onView(withId(recyclerViewId))
    //                .check(matches(allOf(isDisplayed(), hasMinimumChildCount(minCount))))
    //            return onView(withId(recyclerViewId)) // success
    //        } catch (e: Throwable) {
    //            // ignore and retry
    //        }
    //        Thread.sleep(200)
    //    } while (System.currentTimeMillis() < endTime)
    //
    //    throw AssertionError("RecyclerView did not reach $minCount items within $timeout ms")
    //}


}


/**
 * Disables animations on the device for the duration of tests
 */
//class DisableAnimationsRule : TestRule {
//
//    override fun apply(base: Statement, description: Description?): Statement {
//        return object : Statement() {
//            override fun evaluate() {
//                val context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
//                val resolver: ContentResolver = context.contentResolver
//
//                // Disable animations
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                    Settings.Global.putFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 0f)
//                    Settings.Global.putFloat(resolver, Settings.Global.TRANSITION_ANIMATION_SCALE, 0f)
//                    Settings.Global.putFloat(resolver, Settings.Global.WINDOW_ANIMATION_SCALE, 0f)
//                }
//
//                try {
//                    base.evaluate()
//                } finally {
//                    // Optionally restore animations after test
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                        Settings.Global.putFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
//                        Settings.Global.putFloat(resolver, Settings.Global.TRANSITION_ANIMATION_SCALE, 1f)
//                        Settings.Global.putFloat(resolver, Settings.Global.WINDOW_ANIMATION_SCALE, 1f)
//                    }
//                }
//            }
//        }
//    }
//}


//class DisableAnimationsRule : ExternalResource() {
//    override fun before() {
//        val context = InstrumentationRegistry.getInstrumentation().targetContext
//        Settings.Global.putFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 0f)
//        Settings.Global.putFloat(context.contentResolver, Settings.Global.TRANSITION_ANIMATION_SCALE, 0f)
//        Settings.Global.putFloat(context.contentResolver, Settings.Global.WINDOW_ANIMATION_SCALE, 0f)
//    }
//}