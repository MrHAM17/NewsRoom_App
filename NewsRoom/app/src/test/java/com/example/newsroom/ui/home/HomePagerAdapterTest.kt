package com.example.newsroom.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.newsroom.ui.home.news.NewsListFragment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE) // avoids requiring AndroidManifest.xml
class HomePagerAdapterTest {

    private lateinit var adapter: HomePagerAdapter
    private val categories = listOf("general", "business", "technology")

    @Before
    fun setup() {
        // Create a dummy activity
        val activity = Robolectric.buildActivity(FragmentActivity::class.java).setup().get()

        // Create a fragment and attach it to the activity
        val fragment = Fragment()
        activity.supportFragmentManager.beginTransaction().add(fragment, "dummy").commitNow()

        // Pass the attached fragment to the adapter
        adapter = HomePagerAdapter(fragment, categories)
    }

    @Test
    fun `getItemCount returns correct number of categories`() {
        assertEquals(categories.size, adapter.itemCount)
    }

    @Test
    fun `createFragment returns NewsListFragment with correct argument`() {
        val fragment = adapter.createFragment(0)
        assertTrue(fragment is NewsListFragment)

        // Check arguments
        val args = fragment.arguments
        assertEquals("general", args?.getString("arg_category"))
    }

    @Test
    fun `createFragment returns correct fragment for other positions`() {
        categories.forEachIndexed { index, category ->
            val fragment = adapter.createFragment(index)
            assertTrue(fragment is NewsListFragment)
            val args = fragment.arguments
            assertEquals(category, args?.getString("arg_category"))
        }
    }
}
