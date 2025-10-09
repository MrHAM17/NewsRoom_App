package com.example.newsroom.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.newsroom.data.remote.model.dto.Article
import com.example.newsroom.databinding.ItemArticleBinding

class NewsAdapter: ListAdapter<Article, NewsAdapter.VH>(Diff) {

    // click to navigate to detailed news, share, bookmark, etc.
    var onClick: ((Article) -> Unit)? = null

    object Diff: DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Article, newItem: Article) = oldItem == newItem
    }

    inner class VH(val b: ItemArticleBinding): RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.b.title.text = item.title
        holder.b.source.text = item.sourceName ?: ""

//        Glide.with(holder.b.image).load(item.imageUrl).into(holder.b.image)
        // ✅ Optimized image loading for RecyclerView
        if (!item.imageUrl.isNullOrEmpty()) {
            Glide.with(holder.b.image)
                .load(item.imageUrl)
                .override(400, 300) // Fixed size for list items
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL) // cache for smooth scrolling
                .transition(DrawableTransitionOptions.withCrossFade(200)) // Smoother transitions
//                .placeholder(com.example.newsroom.R.drawable.image_placeholder_rounded) // show temp image
                .into(holder.b.image)
        } else {
//            holder.b.image.setImageResource(com.example.newsroom.R.drawable.ic_launcher_background)
            holder.b.image.setImageResource(com.example.newsroom.R.drawable.image_placeholder_rounded)
        }

        holder.b.root.setOnClickListener {
            onClick?.invoke(item)  // use the class-level property
        }
    }

}

