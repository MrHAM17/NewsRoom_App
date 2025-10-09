package com.example.newsroom

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.newsroom.core.theme.ThemeMode
import com.example.newsroom.databinding.ActivityMainBinding
import com.example.newsroom.core.workers.NewsSyncWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import com.example.newsroom.core.theme.ThemeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val themeViewModel: ThemeViewModel by viewModels()
    private var themeChangeDebounce: kotlinx.coroutines.Job? = null // Debounce theme changes

    override fun onCreate(savedInstanceState: Bundle?) {
//        val splash = installSplashScreen()
//        installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                themeViewModel.themeMode.collect { mode ->
                    themeChangeDebounce?.cancel()
                    themeChangeDebounce = lifecycleScope.launch {
                        delay(100) // ✅ Debounce rapid theme changes
                        withContext(Dispatchers.Main) {
                            when (mode) {
                                ThemeMode.SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                                ThemeMode.LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                                ThemeMode.DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                            }
                        }
                    }
                }
            }
        }


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment?
        val navController = navHostFragment?.navController
        if (navController != null)
        { setupWithNavController(binding.bottomNav, navController)
        } else {
//            Log.e("MainActivity", "NavController is null!")
        }
//        val navController = findNavController(R.id.nav_host)
//        setupWithNavController(binding.bottomNav, navController)


//        Log.d("API_KEY_DEBUG", "API Key = ${BuildConfig.NEWS_API_KEY}")


//        // ✅ Call WorkManager, Delay non-critical work until after first render
//        scheduleNewsSync()
        binding.root.post { scheduleNewsSync() }

        // ✅ Hide bottom nav for detail
        val fragmentsToHideBottomNav = setOf( R.id.detailFragment ) // add more if needed
        navController?.addOnDestinationChangedListener { _, destination, _ ->
            if (fragmentsToHideBottomNav.contains(destination.id))  {  hideBottomNav()  }
            else {  showBottomNav()  }
        }

        // ✅ Handle widget/cold-launch intent
//        val extras = intent?.extras
//        Log.d("MainActivityDebug", "onCreate extras = $extras")
        handleWidgetIntent(intent)
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // handle when Activity is reused (warm start)
//        val extras = intent?.extras
//        Log.d("MainActivityDebug", "onNewIntent extras=$extras")
        handleWidgetIntent(intent)
    }

    //    Make WorkManager initialization lazy
    private var workRequestInitialized = false
    private fun scheduleNewsSync() {
        if (!workRequestInitialized) {
            workRequestInitialized = true

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        //  ✅ For testing: run immediately
//        val testRequest = OneTimeWorkRequestBuilder<NewsSyncWorker>()
//            .setConstraints(constraints)
//            .build()
//        WorkManager.getInstance(this).enqueue(testRequest)

//            val request = PeriodicWorkRequestBuilder<NewsSyncWorker>(6, TimeUnit.HOURS)
            val request = PeriodicWorkRequestBuilder<NewsSyncWorker>(12, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "news_sync",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }


    private fun hideBottomNav() {
        binding.bottomNav.animate()
            .translationY(binding.bottomNav.height.toFloat())
            .setDuration(200)
            .start()
    }

    private fun showBottomNav() {
        binding.bottomNav.animate()
            .translationY(0f)
            .setDuration(200)
            .start()
    }

    private fun handleWidgetIntent(intent: Intent?) {
        if (intent == null) return
        val url = intent.getStringExtra("arg_url") ?: return

        val bundle = Bundle().apply {
            putString("arg_url", url)
            putString("arg_title", intent.getStringExtra("arg_title"))
            putString("arg_source", intent.getStringExtra("arg_source"))
            putString("arg_image", intent.getStringExtra("arg_image"))
            putString("arg_author", intent.getStringExtra("arg_author"))
            putString("arg_publishedAt", intent.getStringExtra("arg_publishedAt"))
            putString("arg_description", intent.getStringExtra("arg_description"))
            putString("arg_content", intent.getStringExtra("arg_content"))
            putString("arg_article_id", intent.getStringExtra("arg_article_id"))
            putString("arg_bookmark_action", intent.getStringExtra("arg_bookmark_action"))
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host) as? NavHostFragment
        navHostFragment?.navController?.navigate(R.id.detailFragment, bundle)
    }

}
