package com.example.newsroom.core.util.glide


import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.module.AppGlideModule

@GlideModule
class NewsGlideModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        // ~50MB disk cache for images
        builder.setDiskCache(InternalCacheDiskCacheFactory(context, 50 * 1024 * 1024))
    }
}
