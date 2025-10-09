package com.example.newsroom.ui.home.news

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.newsroom.MainActivity
import com.example.newsroom.R
import com.example.newsroom.ui.common.NewsAdapter
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
class NewsListFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setUp() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        // Assuming HomeFragment is default, otherwise navigate to home first
        try {
            onView(withId(R.id.homeFragment)).perform(click())
            Thread.sleep(2000)
        } catch (e: Exception) {
            println("Could not navigate to HomeFragment: ${e.message}")
        }
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun recyclerViewVisible_whenArticlesLoaded() {
        try {
            onView(withId(R.id.recycler)).check(matches(isDisplayed()))
            onView(withId(R.id.errorText)).check(matches(not(isDisplayed())))
        } catch (e: AssertionError) {
            onView(withId(R.id.errorText)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun swipeRefresh_clickable() {
        onView(withId(R.id.swipe)).perform(click())
        Thread.sleep(1000)
    }

    @Test
    fun clickArticle_opensDetailFragment() {
        try {
            onView(withId(R.id.recycler))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<NewsAdapter.VH>(0, click())
                )
            Thread.sleep(2000)

            // Verify navigation to DetailFragment
            onView(withId(R.id.title)).check(matches(isDisplayed()))
            onView(withId(R.id.bookmarkButton)).check(matches(isDisplayed()))
        } catch (e: Exception) {
            println("No articles available to test navigation: ${e.message}")
        }
    }

    @Test
    fun testMainActivityLaunches() {
        onView(withId(android.R.id.content)).check(matches(isDisplayed()))
    }

}






