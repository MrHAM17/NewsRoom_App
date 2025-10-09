package com.example.newsroom.ui.home

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.newsroom.MainActivity
import com.example.newsroom.R
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class HomeFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setUp() {
        hiltRule.inject()
        scenario = ActivityScenario.launch(MainActivity::class.java)
        // Navigate to HomeFragment
        onView(withId(R.id.homeFragment)).perform(click())
        Thread.sleep(2000) // Wait for fragment to load
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    /** Test 1: TabLayout and ViewPager are displayed */
    @Test
    fun tabsAndViewPager_displayCorrectly() {
        onView(withId(R.id.tabs)).check(matches(isDisplayed()))
        onView(withId(R.id.viewPager)).check(matches(isDisplayed()))
    }

}

