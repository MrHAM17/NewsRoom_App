package com.example.newsroom.ui.common.detail

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.newsroom.R
import com.example.newsroom.data.remote.model.dto.Article
import com.example.newsroom.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment: Fragment() {

//    private val args: DetailFragmentArgs by navArgs()  // ✅ now generated
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        val url = args.argUrl
//        val id = args.argArticleId
//        val title = args.argTitle
//        val source = args.argSource
//        val image = args.argImage
//    }
    companion object{
        private const val ARG_ARTICLE_id = "arg_article_id"
        private const val ARG_URL = "arg_url"
        private const val ARG_TITLE = "arg_title"
        private const val ARG_SOURCE = "arg_source"
        private const val ARG_IMAGE = "arg_image"
        private const val ARG_AUTHOR = "arg_author"
        private const val ARG_PUBLISHEDAT = "arg_publishedAt"
        private const val ARG_DESCRIPTION = "arg_description"
        private const val ARG_CONTENT = "arg_content"


        private const val ARG_BOOKMARK_ACTION = "arg_bookmark_action"


    fun args(article: Article, bookmarkAction: String = "add") = bundleOf(
            ARG_ARTICLE_id to article.id,
            ARG_URL to article.url,
            ARG_TITLE to article.title,
            ARG_SOURCE to article.sourceName,
            ARG_IMAGE to article.imageUrl,
            ARG_AUTHOR to article.author,
            ARG_PUBLISHEDAT to article.publishedAtUtc,
            ARG_DESCRIPTION to article.description,
            ARG_CONTENT to article.content,

            ARG_BOOKMARK_ACTION to bookmarkAction

    )
    }

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState) // <-- Optional but good practice

        Log.d("DetailDebug", "Opened detail with args=${arguments}")

        val url = arguments?.getString(ARG_URL)!!
        val title = arguments?.getString(ARG_TITLE)!!
        val source = arguments?.getString(ARG_SOURCE)
        val id = arguments?.getString(ARG_ARTICLE_id)!!
        val image = arguments?.getString(ARG_IMAGE)
        val author = arguments?.getString(ARG_AUTHOR)
        val publishedAt = arguments?.getString(ARG_PUBLISHEDAT)
        val description = arguments?.getString(ARG_DESCRIPTION)
        val content = arguments?.getString(ARG_CONTENT)

        val bookmarkAction = arguments?.getString(ARG_BOOKMARK_ACTION) ?: "add"



//        Log.d("DetailDebug", "title: $title")
//        Log.d("DetailDebug", "desc: $description")
//        Log.d("DetailDebug", "content: $content")
//        Log.d("DetailDebug", "url: $url")
//        Log.d("DetailDebug", "image: $image")
//        Log.d("DetailDebug", "source: $source")
//        Log.d("DetailDebug", "author: $author")
//        Log.d("DetailDebug", "publishedAt: $publishedAt")
//        Log.d("DetailDebug", "id: $id")



        if (bookmarkAction == "add")
        {
            /*
                suspend fun return asynchronously, so can’t just write :
                     if (viewModel.checkIsBookmarked(id)) { ... } // ❌ won't work
                Instead, you trigger the check:
                     viewModel.checkIsBookmarked(id)
                And react when the result arrives:
                     viewModel.isBookmarked.observe(viewLifecycleOwner) { bookmarked -> if (bookmarked) {...}}
            */

            viewModel.checkIsBookmarked(id)

            viewModel.isBookmarked.observe(viewLifecycleOwner) { bookmarked ->
                if (bookmarked) {
                    // Already saved → disable button
                    binding.bookmarkButton.text = getString(R.string.already_bookmarked)
                    binding.bookmarkButton.setIconResource(R.drawable.ic_bookmark_added)
                    binding.bookmarkButton.isSelected = false  // blue
                    binding.bookmarkButton.isEnabled = false
                }
                else {
                    // Not saved yet → allow adding
                    binding.bookmarkButton.text = getString(R.string.save_bookmark)
                    binding.bookmarkButton.setIconResource(R.drawable.ic_bookmark_add)
                    binding.bookmarkButton.isEnabled = true
                }
            }
        }
        else if (bookmarkAction == "remove")
        {
            binding.bookmarkButton.text = getString(R.string.remove_bookmark)
            binding.bookmarkButton.setIconResource(R.drawable.ic_bookmark_remove)
            binding.bookmarkButton.isSelected = true   // red
            binding.bookmarkButton.isEnabled = true

        }

//        // ✅ Load image (using Glide or Picasso)
//        Glide.with(this)
//            .load(image)
//            .placeholder(R.drawable.ic_launcher_background)
//            .into(binding.image)
        // ✅ Optimized image loading with proper error handling
        if (!image.isNullOrEmpty()) {
            Glide.with(this)
                .load(image)
                .placeholder(R.drawable.image_placeholder_rounded)
                .override(800, 600) // ✅ Limit image size for detail view
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.image_placeholder_rounded) // ✅ Fallback on error
                .into(binding.image)
        } else {
//            binding.image.setImageResource(R.drawable.ic_launcher_background)
            binding.image.setImageResource(R.drawable.image_placeholder_rounded)
        }

        // ✅ Set text content
        binding.title.text = title
        binding.source.text = source ?: "Unknown source"
        binding.author.text = author ?: "Unknown author"
        binding.publishedAt.text = publishedAt ?: ""
        binding.description.text = description ?:  getString(R.string.no_description)
        binding.content.text = content ?: getString(R.string.no_content)

        binding.openButton.setOnClickListener{
            CustomTabsIntent.Builder().build().launchUrl(requireContext(), url.toUri())
        }

        binding.bookmarkButton.apply {
            when (bookmarkAction) {
                "remove" -> {
//                    isSelected = true

                    setOnClickListener {
                        AlertDialog.Builder(requireContext())
                            .setTitle(getString(R.string.confirm_remove_title))
                            .setMessage(getString(R.string.confirm_remove_message))
                            .setPositiveButton(getString(R.string.remove)) { _, _ ->
                                viewModel.removeBookmark(id)
                            }
                            .setNegativeButton(getString(R.string.cancel), null)
                            .show()
                    }

                    // Pop back when removed
                    viewModel.bookmarkRemoved.observe(viewLifecycleOwner) { removed ->
                        if (removed) { findNavController().popBackStack() }  }
                }
                else -> {
//                    isSelected = false

                    setOnClickListener {
                        viewModel.saveBookmark(
                            id = id,
                            title = requireArguments().getString(ARG_TITLE)!!,
                            url = url,
                            source = requireArguments().getString(ARG_SOURCE),
                            image = requireArguments().getString(ARG_IMAGE)
                        )
                    }
                }
            }
        }

        binding.shareButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
//                Intent.setType = "text/plain"
                type = "text/plain"   // ✅ this is correct
                putExtra(Intent.EXTRA_TEXT, url)
                putExtra(Intent.EXTRA_SUBJECT, requireArguments().getString(ARG_TITLE))
            }
            startActivity(Intent.createChooser(intent, getString(R.string.share)))
        }
    }

    override fun onDestroyView() { _binding = null; super.onDestroyView() }

}