package com.example.newsroom

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.testing.WorkManagerTestInitHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import android.content.Context

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setup() {
        hiltRule.inject()  // Hilt first

        val context = ApplicationProvider.getApplicationContext<Context>()
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)

        // Launch activity manually AFTER Hilt + WorkManager are ready
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }



    @Test
    fun bottomNavIsVisibleInitially() {
        scenario.onActivity { activity ->
            val bottomNav = activity.findViewById<BottomNavigationView>(R.id.bottomNav)
            assertThat(bottomNav.translationY).isEqualTo(0f)
        }
    }

//    @Test
//    fun bottomNavHidesOnDetailFragmentNavigation() {
//        scenario.onActivity { activity ->
//            val bottomNav = activity.findViewById<BottomNavigationView>(R.id.bottomNav)
//            assertThat(bottomNav.translationY).isEqualTo(0f)
//
//            val bundle = Bundle().apply { putString("arg_url", "https://example.com") }
//            val navHostFragment =
//                activity.supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
//            navHostFragment.navController.navigate(R.id.detailFragment, bundle)
//
//            Thread.sleep(300) // wait for animation
//            assertThat(bottomNav.translationY)
//                .isWithin(1f)
//                .of(bottomNav.height.toFloat())
//
//            navHostFragment.navController.navigateUp()
//            Thread.sleep(300)
//            assertThat(bottomNav.translationY)
//                .isWithin(1f)
//                .of(0f)
//        }
//    }

    @Test
    fun scheduleNewsSync_enqueuesPeriodicWork() {
        scenario.onActivity { activity ->
            val workManager = WorkManager.getInstance(activity)
            val workInfos = workManager.getWorkInfosForUniqueWork("news_sync").get()
            assertThat(workInfos).isNotEmpty()
            assertThat(workInfos[0].state.name).isEqualTo("ENQUEUED")
        }
    }

    @Test
    fun handleWidgetIntent_navigatesToDetailFragment() {
        // Prepare intent BEFORE launching activity
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            MainActivity::class.java
        ).apply {
            putExtra("arg_url", "https://example.com")
            putExtra("arg_title", "Test Title")
            putExtra("arg_article_id", "Test Id")
            putExtra("arg_bookmark_action", "add")

        }

        scenario = ActivityScenario.launch<MainActivity>(intent)  // Launch with intent

        scenario.onActivity { activity ->
            val navHostFragment =
                activity.supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment

            // Check navigation destination
            val currentDest = navHostFragment.navController.currentDestination
            assertThat(currentDest?.label).isEqualTo("Detail") // safer than R.id check
        }
    }


    @Test
    fun themeMode_changesNightMode() {
        scenario.onActivity {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        assertThat(AppCompatDelegate.getDefaultNightMode())
            .isEqualTo(AppCompatDelegate.MODE_NIGHT_YES)

        scenario.onActivity {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        assertThat(AppCompatDelegate.getDefaultNightMode())
            .isEqualTo(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
