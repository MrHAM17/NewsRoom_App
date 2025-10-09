// No test needed: App class only sets up Hilt and WorkManager; behavior verified at runtime.

package com.example.newsroom

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.DEBUG) // optional
            .build()
}


//package com.example.newsroom
//
//import android.app.Application
//import dagger.hilt.android.HiltAndroidApp
//
//@HiltAndroidApp
//class App: Application()