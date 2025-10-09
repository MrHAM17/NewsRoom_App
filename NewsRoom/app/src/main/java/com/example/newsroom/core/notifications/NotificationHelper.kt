package com.example.newsroom.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.newsroom.R

object NotificationHelper {

    private const val CHANNEL_ID = "news_sync"

    fun notifySync(context: Context, text: String) {
        val my_notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= 26) {
            my_notificationManager.createNotificationChannel(
             NotificationChannel(
                 CHANNEL_ID, "News_Sync",
             NotificationManager.IMPORTANCE_LOW))
        }

        val my_notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_sync)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(text)
            .setOngoing(false)
            .build()

        my_notificationManager.notify(1001, my_notification)
//        Log.e("my_Notification", "Notification sent!")
    }
}