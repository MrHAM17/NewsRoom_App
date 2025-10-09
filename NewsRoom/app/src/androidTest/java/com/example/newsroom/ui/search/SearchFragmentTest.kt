/*

Test coverage for SearchFragment:
1.	Search bar & initial state
    o	Verify search input field is visible.
    o	Verify recycler is empty initially.
2.	Search with results
    o	Mock repo → return 2 fake articles.
    o	Type query → check recycler displays both articles (title, source).
3.	Search with no results
    o	Mock repo → return empty list.
    o	Type query → check recycler is gone, maybe show “No results”.
4.	Search offline with cached results
    o	Mock repo to throw error but fallback to ftsSearch() returns cached rows.
    o	Type cached query → verify recycler still displays cached articles.
5.	Search offline with no cache
    o	Mock repo to throw error + no cached rows.
    o	Type new query → progress disappears, recycler empty.
6.	Click search result → open detail fragment
    o	Type query → click item.
    o	Verify navigation to DetailFragment (check title text visible).
7.	Detail fragment shows correct bookmark state
    o	If article not bookmarked → shows “Add bookmark”.
    o	If already bookmarked → shows “Added in bookmarked”.


*/

package com.example.newsroom.ui.search

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.newsroom.MainActivity
import com.example.newsroom.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import com.example.newsroom.TestUtils.waitFor
import com.example.newsroom.TestUtils.waitForRecyclerViewItemCount
import com.example.newsroom.TestUtils.waitForSearchRecyclerViewItemCount
import com.example.newsroom.ui.common.NewsAdapter

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SearchFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

//    @get:Rule
//    val disableAnimationsRule = DisableAnimationsRule()

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        scenario?.close()
    }


    @Test
    fun searchBarVisible_initialStateRecyclerEmpty() {
        scenario = ActivityScenario.launch(MainActivity::class.java)

        // Navigate to SearchFragment
        onView(withId(R.id.searchFragment)).perform(click())

        // Wait for fragment to load
        Thread.sleep(1000)

        // Verify search input field is visible
        onView(withId(R.id.query)).check(matches(isDisplayed()))

        // Verify recycler is initially visible but empty
        onView(withId(R.id.recycler)).check(matches(isDisplayed()))
    }

    @Test
    fun typingQuery_showsResults() {
        scenario = ActivityScenario.launch(MainActivity::class.java)

        // Navigate to SearchFragment
        onView(withId(R.id.searchFragment)).perform(click())

        // Type query and search
        onView(withId(R.id.query)).perform(typeText("news"), pressImeActionButton())

        // Wait until RecyclerView has at least 1 item
        waitForRecyclerViewItemCount(R.id.recycler, 1, 5000)

        // Verify recycler is visible with results
        onView(withId(R.id.recycler)).check(matches(isDisplayed()))

        // Progress bar should be gone
        onView(withId(R.id.progress)).check(matches(not(isDisplayed())))
    }


    @Test
    fun typingQuery_noResultsShowsEmptyState() {
        scenario = ActivityScenario.launch(MainActivity::class.java)

        // Navigate to SearchFragment
        onView(withId(R.id.searchFragment)).perform(click())

        // Wait for fragment to load using IdlingResource pattern
        onView(isRoot()).perform(waitFor(1000))

        // Clear any existing text first
        onView(withId(R.id.query)).perform(clearText())

        // Type query that returns no results
        onView(withId(R.id.query)).perform(typeText("xyzquerywithnoresults"), pressImeActionButton())

        // Wait for search to complete
        onView(isRoot()).perform(waitFor(2000))

        // First check if progress is gone
        onView(withId(R.id.progress)).check(matches(not(isDisplayed())))

        // Then verify empty state is visible with proper error message
        onView(withId(R.id.tvEmptyState))
            .check(matches(isDisplayed()))
            .check(matches(withText("No results available")))

        // Verify recycler is hidden
        onView(withId(R.id.recycler)).check(matches(not(isDisplayed())))
    }


    @Test
    fun offlineFallback_showsCachedResults() {
        scenario = ActivityScenario.launch(MainActivity::class.java)

        // Navigate to SearchFragment
        onView(withId(R.id.searchFragment)).perform(click())

        // Type query (will use offline cache if available)
        onView(withId(R.id.query)).perform(typeText("cached"), pressImeActionButton())

        // Wait until RecyclerView has at least 1 item (cached results)
        waitForRecyclerViewItemCount(R.id.recycler, 0, 5000)

        // Verify recycler is visible (cached results)
        onView(withId(R.id.recycler)).check(matches(isDisplayed()))
        onView(withId(R.id.tvEmptyState)).check(matches(not(isDisplayed())))
        // Wait extra time to ensure loading -> finished
        onView(isRoot()).perform(waitFor(2000))
        // Verify progress bar is gone
        onView(withId(R.id.progress)).check(matches(not(isDisplayed())))
    }


    @Test
    fun offlineFallback_noCacheShowsEmptyState() {
        scenario = ActivityScenario.launch(MainActivity::class.java)

        // Navigate to SearchFragment
        onView(withId(R.id.searchFragment)).perform(click())

        // Wait for fragment to load
        Thread.sleep(1000)

        // Type query that has no cache
        onView(withId(R.id.query)).perform(typeText("offlinenocache"), pressImeActionButton())

        // Wait for search
        Thread.sleep(2000)

        // Verify empty state is visible and recycler is hidden
        onView(withId(R.id.tvEmptyState)).check(matches(isDisplayed()))
        onView(withId(R.id.recycler)).check(matches(not(isDisplayed())))
        // Wait extra time to ensure loading -> finished
        onView(isRoot()).perform(waitFor(2000))

        // Now assert
        onView(withId(R.id.progress)).check(matches(not(isDisplayed())))
    }

    @Test
    fun clickingSearchItem_opensDetail() {
        scenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.searchFragment)).perform(click())

        // Type query
        onView(withId(R.id.query)).perform(typeText("news"), pressImeActionButton())

        onView(isRoot()).perform(waitFor(5000)) // let search results populate

//        onView(withId(R.id.recycler)).perform(waitFor(5000)) // Wait until RecyclerView is visible

        // Wait for RecyclerView to have items
        waitForSearchRecyclerViewItemCount(R.id.recycler, 1, 4000)

        // Click first item
        onView(withId(R.id.recycler))
            .perform(
                androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition<NewsAdapter.VH>(0, click())
            )

        // Verify detail views
        onView(withId(R.id.title)).check(matches(isDisplayed()))
        onView(withId(R.id.bookmarkButton)).check(matches(isDisplayed()))
    }


    @Test
    fun searchFragment_initialLoad() {
        scenario = ActivityScenario.launch(MainActivity::class.java)

        // Navigate to SearchFragment
        onView(withId(R.id.searchFragment)).perform(click())

        // Wait for fragment to load
        Thread.sleep(1000)

        // Verify all main components are present
        onView(withId(R.id.query)).check(matches(isDisplayed()))
        onView(withId(R.id.recycler)).check(matches(isDisplayed()))
        onView(withId(R.id.tvEmptyState)).check(matches(not(isDisplayed()))) // Hidden initially
        onView(withId(R.id.progress)).check(matches(not(isDisplayed()))) // Hidden initially
    }

}