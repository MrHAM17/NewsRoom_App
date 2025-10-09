package com.example.newsroom.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.newsroom.R
import com.example.newsroom.data.remote.model.dto.Article
import dagger.hilt.android.EntryPointAccessors
import com.example.newsroom.core.di.WidgetEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import java.net.HttpURLConnection
import java.net.URL

class NewsRemoteViewsFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {

    private var articles: List<Article> = emptyList()

    override fun onCreate() {}

    override fun onDataSetChanged() {
        val entryPoint = EntryPointAccessors.fromApplication(context, WidgetEntryPoint::class.java)
        val repo = entryPoint.newsRepository()
        runBlocking {
            val result = repo.topHeadlines("general").firstOrNull()
            articles = result?.getOrNull()?.take(5) ?: emptyList()
        }
    }

    override fun getCount() = articles.size

    override fun getViewAt(position: Int): RemoteViews {
        val article = articles[position]
        val rv = RemoteViews(context.packageName, R.layout.news_widget_item)
        rv.setTextViewText(R.id.widget_item_title, article.title)

        if (!article.imageUrl.isNullOrEmpty()) {
            val bitmap = loadBitmapFromUrl(article.imageUrl!!)
            if (bitmap != null) {
                rv.setImageViewBitmap(R.id.widget_item_image, bitmap)
            } else {
                rv.setImageViewResource(
                    R.id.widget_item_image,
                    R.drawable.image_placeholder_rounded
                )
            }
        } else {
            rv.setImageViewResource(
                R.id.widget_item_image,
                R.drawable.image_placeholder_rounded
            )
        }

        val fillIntent = Intent().apply {
            putExtra("arg_article_id", article.id)
            putExtra("arg_url", article.url)
            putExtra("arg_title", article.title)
            putExtra("arg_source", article.sourceName)
            putExtra("arg_image", article.imageUrl)
            putExtra("arg_author", article.author)
            putExtra("arg_publishedAt", article.publishedAtUtc)
            putExtra("arg_description", article.description)
            putExtra("arg_content", article.content)
            putExtra("arg_bookmark_action", "add")
        }
        rv.setOnClickFillInIntent(R.id.widget_item_root, fillIntent)
        return rv
    }
    fun loadBitmapFromUrl(url: String): Bitmap? {
        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            null
        }
    }


    override fun getLoadingView() = null
    override fun getViewTypeCount() = 1
    override fun getItemId(position: Int) = position.toLong()
    override fun hasStableIds() = true
    override fun onDestroy() {}
}


