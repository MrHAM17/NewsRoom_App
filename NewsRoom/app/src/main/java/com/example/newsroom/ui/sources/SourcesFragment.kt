package com.example.newsroom.ui.sources

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsroom.R
import com.example.newsroom.databinding.FragmentSourcesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.observeOn
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SourcesFragment: Fragment() {
    private var _binding: FragmentSourcesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SourcesViewModel by viewModels()
    private val adapter = SourcesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSourcesBinding.inflate(inflater, container, false);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = adapter
//        viewModel.state.observe(viewLifecycleOwner) { /* if converting to LiveData */ }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.progress.visibility = if (state.loading) View.VISIBLE else View.GONE
                    adapter.submitList(state.list)
                }
            }
        }
        viewModel.load()
    }


    override fun onDestroyView() {
        _binding = null;
        super.onDestroyView()
    }
}