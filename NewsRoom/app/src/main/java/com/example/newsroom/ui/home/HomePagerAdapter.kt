package com.example.newsroom.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.newsroom.ui.home.news.NewsListFragment

class HomePagerAdapter
    (parent: Fragment, private val categories: List<String>)
    : FragmentStateAdapter(parent)
{
    override fun getItemCount() = categories.size
    override fun createFragment(position: Int): Fragment = NewsListFragment.newInstance(categories[position])
}