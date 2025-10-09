package com.example.newsroom.ui.home.news

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsroom.databinding.FragmentNewsListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController
import com.example.newsroom.ui.common.NewsAdapter
import com.example.newsroom.ui.common.detail.DetailFragment

@AndroidEntryPoint
class NewsListFragment: Fragment() {
//    companion object{
//        private const val ARG_CATEGORY = "arg_Category"
//
//        fun newInstance(category: String) : NewsListFragment {
//            val fragment = NewsListFragment()
//            fragment.arguments = bundleOf(ARG_CATEGORY to category)
//            return fragment
//        }
//    }
companion object {
    private const val ARG_CATEGORY = "arg_category"
    fun newInstance(category: String) = NewsListFragment().apply {
        arguments = bundleOf(ARG_CATEGORY to category)
    }
}

    private var _binding: FragmentNewsListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewsListViewModel by viewModels()
    private val adapter = NewsAdapter()

    // variable to hold scroll state
    private var scrollState: Parcelable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewsListBinding.inflate(inflater, container, false)
        return binding.root
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
//        binding.recycler.adapter = adapter
//        binding.swipe.setOnRefreshListener { reload() }
//        reload()
//    }
//    private fun reload() {
//        val category = requireArguments().getString(ARG_CATEGORY) ?: "general"
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewModel.loadTopHeadlines(category).collect { state ->
//                binding.swipe.isRefreshing = state.loading
//                adapter.submitList(state.articles)
//            }
//        }
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = adapter

//        binding.recycler.setHasFixedSize(true) // allow LayoutManager to auto-restore
        binding.recycler.setHasFixedSize(false) // Use this if your item sizes are consistent // Or remove entirely


        // ✅ Glide image preloader
        val preloader = com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader(
            com.bumptech.glide.Glide.with(this),
            object : com.bumptech.glide.ListPreloader.PreloadModelProvider<String> {
                override fun getPreloadItems(position: Int): List<String> {
                    val item = adapter.currentList.getOrNull(position)
                    return item?.imageUrl?.let { listOf(it) } ?: emptyList()
                }

                override fun getPreloadRequestBuilder(item: String)
                        = com.bumptech.glide.Glide.with(this@NewsListFragment).load(item)

                /*
                    Replacement of above 2 fun by below 2 fun @ Paging 3 implementation...
                */
//                override fun getPreloadItems(position: Int) =
//                    pagingAdapter.peek(position)?.imageUrl?.let { listOf(it) } ?: emptyList()
//                override fun getPreloadRequestBuilder(url: String) =
//                    com.bumptech.glide.Glide.with(this@NewsListFragment).load(url)
            },
            com.bumptech.glide.util.ViewPreloadSizeProvider<String>(),10 // number of items to preload
        )
        binding.recycler.addOnScrollListener(preloader)




        val category = requireArguments().getString(ARG_CATEGORY) ?: "general"
        viewModel.loadTopHeadlines(category, requireContext())

        // Observe state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                binding.swipe.isRefreshing = state.loading

                if (state.articles.isNotEmpty()) {
                    // Always show articles if we have cached them
                    binding.recycler.visibility = View.VISIBLE
                    binding.errorText.visibility = View.GONE
                } else if (!state.error.isNullOrEmpty()) {
                    // Show error only if there’s no data
                    binding.errorText.visibility = View.VISIBLE
                    binding.errorText.text = state.error
                    binding.recycler.visibility = View.GONE

                    // optionally, you can still toast or log the error
                    state.error.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Initial empty state
                    binding.errorText.visibility = View.GONE
                    binding.recycler.visibility = View.GONE
                }

                // ✅ Single point of updating list
                adapter.submitList(state.articles)

                //  restore scroll AFTER data submitted
                scrollState?.let {
                    binding.recycler.layoutManager?.onRestoreInstanceState(it)
                    scrollState = null
                }
            }
        }

        binding.swipe.setOnRefreshListener {
            viewModel.loadTopHeadlines(category, requireContext())
        }

        adapter.onClick = { article ->
//            val action = com.example.newsroom.R.id.action_home_to_detail
//            findNavController().navigate(action, DetailFragment.args(article))

            val action = com.example.newsroom.R.id.action_home_to_detail
            findNavController().navigate(action,DetailFragment.args(article, "add") )
        }

    }

    // Save scroll state when fragment goes away
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("scroll_state", scrollState)
    }

    // Restore scroll state when fragment recreated
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        scrollState =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            savedInstanceState?.getParcelable("scroll_state", Parcelable::class.java)  }
            else {
            @Suppress("DEPRECATION")
            savedInstanceState?.getParcelable("scroll_state")
            }

    }

    override fun onDestroyView() {
        // Save scroll state before clearing binding
        scrollState = binding.recycler.layoutManager?.onSaveInstanceState()

        _binding = null
        super.onDestroyView()
    }
}