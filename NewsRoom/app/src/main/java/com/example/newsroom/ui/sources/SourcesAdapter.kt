package com.example.newsroom.ui.sources

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.newsroom.data.remote.model.dto.SourceDto
import com.example.newsroom.databinding.ItemSourceBinding

class SourcesAdapter: ListAdapter<SourceDto, SourcesAdapter.VH>(Diff) {
    object Diff: DiffUtil.ItemCallback<SourceDto>() {
        override fun areItemsTheSame(oldItem: SourceDto, newItem: SourceDto) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: SourceDto, newItem: SourceDto) = oldItem == newItem
    }

    class VH(val binding: ItemSourceBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder( parent: ViewGroup, viewType: Int) =
        VH(ItemSourceBinding.inflate(LayoutInflater.from(parent.context), parent, false))


    override fun onBindViewHolder( holder: VH, position: Int )
    {
        val source = getItem(position)
        holder.binding.name.text = source.name
        holder.binding.description.text = source.description
    }

}