package com.example.newsroom.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import com.example.newsroom.MainActivity
import com.example.newsroom.R

class NewsWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { appWidgetId ->
            val views = RemoteViews(context.packageName, R.layout.news_widget)

            // Bind ListView to service
            val serviceIntent = Intent(context, NewsWidgetService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = Uri.parse("newswidget://service/$appWidgetId")
            }
            views.setRemoteAdapter(R.id.widget_list, serviceIntent)
            views.setEmptyView(R.id.widget_list, R.id.emptyView)

            // Handle clicks
            // template intent: open MainActivity directly when row tapped
            val templateIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                action = Intent.ACTION_VIEW
                data = Uri.parse("newswidget://widget/$appWidgetId")
            }

            val pendingIntentFlags =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                else
                    PendingIntent.FLAG_UPDATE_CURRENT

            val templatePendingIntent = PendingIntent.getActivity(
                context,
                0,
                templateIntent,
                pendingIntentFlags
            )
            views.setPendingIntentTemplate(R.id.widget_list, templatePendingIntent)


            // Refresh the list
            AppWidgetManager.getInstance(context)
                .notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list)
            manager.updateAppWidget(appWidgetId, views)
        }
    }
}