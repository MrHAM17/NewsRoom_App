package com.example.newsroom.ui.sources

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.newsroom.MainActivity
import com.example.newsroom.R
import com.example.newsroom.TestUtils.waitFor
import com.example.newsroom.TestUtils.waitForRecyclerViewItemCount
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SourcesFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        scenario.close()
    }



    @Test
    fun sourcesFragment_loadsAndDisplaysContent() {
        scenario = ActivityScenario.launch(MainActivity::class.java)

        // Navigate to SourcesFragment
        onView(withId(R.id.sourcesFragment)).perform(click())

        // Wait for fragment to load and data to be processed
        Thread.sleep(2000)

        // Verify the recycler view is displayed (main content)
        onView(withId(R.id.recycler)).check(matches(isDisplayed()))

        // Progress bar should be hidden after loading
        onView(withId(R.id.progress)).check(matches(not(isDisplayed())))
    }

    @Test
    fun sourcesFragment_recyclerViewHasContent() {
        scenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.sourcesFragment)).perform(click())

        // Wait for RecyclerView to load data
        waitForRecyclerViewItemCount(R.id.recycler, 0, 5000)

        onView(withId(R.id.recycler)).check(matches(isDisplayed()))
        // Wait extra time to ensure loading -> finished
        onView(isRoot()).perform(waitFor(2000))
        onView(withId(R.id.progress)).check(matches(not(isDisplayed())))
    }

    @Test
    fun navigationToSourcesFragment_worksCorrectly() {
        scenario = ActivityScenario.launch(MainActivity::class.java)

        // Verify activity launched
        onView(withId(android.R.id.content)).check(matches(isDisplayed()))

        // Navigate to SourcesFragment
        onView(withId(R.id.sourcesFragment)).perform(click())

        // Wait for navigation
        Thread.sleep(1000)

        // Verify SourcesFragment main container is displayed
        onView(withId(R.id.recycler)).check(matches(isDisplayed()))
    }


}
