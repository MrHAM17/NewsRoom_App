package com.example.newsroom.widget

import android.content.Intent
import android.widget.RemoteViewsService
import com.example.newsroom.widget.NewsRemoteViewsFactory

class NewsWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent) = NewsRemoteViewsFactory(applicationContext)
}
