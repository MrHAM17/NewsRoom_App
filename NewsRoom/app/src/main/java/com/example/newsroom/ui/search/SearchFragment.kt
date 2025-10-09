package com.example.newsroom.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsroom.R
import com.example.newsroom.databinding.FragmentSearchBinding
import com.example.newsroom.ui.common.NewsAdapter
import com.example.newsroom.ui.common.detail.DetailFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _b: FragmentSearchBinding? = null
    private val b get() = _b!!

    private val vm: SearchViewModel by viewModels()
    private val adapter = NewsAdapter()
    private var debounceJob: Job? = null

    private fun showEmptyState(show: Boolean) {
//        b.tvEmptyState.visibility = if (show) View.VISIBLE else View.GONE
//        b.recycler.visibility = if (show) View.GONE else View.VISIBLE
        if (show) {
            b.tvEmptyState.visibility = View.VISIBLE
            b.recycler.visibility = View.GONE
        } else {
            b.tvEmptyState.visibility = View.GONE
            b.recycler.visibility = View.VISIBLE
        }
    }

//     Show progress bar
    private fun showLoading(show: Boolean) {
        b.progress.visibility = if (show) View.VISIBLE else View.GONE
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _b = FragmentSearchBinding.inflate(inflater, container, false); return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

//         Initialize UI state
        showLoading(false)
        showEmptyState(false)
        adapter.submitList(emptyList())


        b.recycler.layoutManager = LinearLayoutManager(requireContext())
        b.recycler.adapter = adapter

        b.query.doOnTextChanged { text, _, _, _ ->
            debounceJob?.cancel()
            debounceJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(350)
                val q = text?.toString()?.trim().orEmpty()

//                if (q.isEmpty()) return@launch
                if (q.isEmpty()) {
                    // Clear results when query is empty
                    adapter.submitList(emptyList())
                    showLoading(false)
                    showEmptyState(false) // Hide empty state when no query
                    return@launch
                }

                vm.searchEverything(q).collect { state ->
                    showLoading(state.loading)

//                    b.progress.visibility = if (state.loading) View.VISIBLE else View.GONE
//                    adapter.submitList(state.articles)
                    if (!state.loading) {
                        adapter.submitList(state.articles)
                        // Show empty state only when search is complete and no results
                        showEmptyState(state.articles.isEmpty())
                    }

                }
            }
        }

        adapter.onClick = { article ->
//            val action = com.example.newsroom.R.id.action_search_to_detail
//            findNavController().navigate(action, DetailFragment.args(article))

            val action = DetailFragment.args(article, bookmarkAction = "add")
            findNavController().navigate(R.id.action_search_to_detail, action)
        }
    }

    override fun onDestroyView() {
        _b = null
        super.onDestroyView()
    }
}
