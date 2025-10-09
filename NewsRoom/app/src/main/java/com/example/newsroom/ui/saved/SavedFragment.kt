package com.example.newsroom.ui.saved

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsroom.R
import com.example.newsroom.data.remote.model.dto.Article
import com.example.newsroom.data.remote.model.dto.toArticle
import com.example.newsroom.databinding.FragmentSavedBinding
import com.example.newsroom.ui.common.NewsAdapter
import com.example.newsroom.ui.common.detail.DetailFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SavedFragment : Fragment() {

    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SavedViewModel by viewModels()

    private lateinit var adapter: NewsAdapter

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {
        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = NewsAdapter().apply {
            onClick = { article: Article ->
                val action = DetailFragment.args(article, bookmarkAction = "remove")
                findNavController().navigate(R.id.action_saved_to_detail, action)
            }
        }

        binding.rvBookmarks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBookmarks.adapter = adapter

        // Observe bookmarks
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.bookmarks.collectLatest { list ->
                adapter.submitList(list.map { it.toArticle() })

                binding.tvEmptyState.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                binding.rvBookmarks.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
