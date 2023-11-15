package com.renbin.bookproject.ui.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.renbin.bookproject.R
import com.renbin.bookproject.databinding.FragmentBookDetailsBinding
import com.renbin.bookproject.ui.base.BaseFragment
import com.renbin.bookproject.ui.book.viewModel.BookDetailsViewModel
import com.renbin.bookproject.core.util.Utility.formatTimestamp
import com.renbin.bookproject.core.util.Utility.loadPdf
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookDetailsFragment : BaseFragment<FragmentBookDetailsBinding>() {
    override val viewModel: BookDetailsViewModel by viewModels()
    private val args: BookDetailsFragmentArgs by navArgs()
    private lateinit var url: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBookDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupUIComponents() {
        super.setupUIComponents()

        viewModel.getBook(args.bookId2)
        binding.run {
            ibBack.setOnClickListener {
                navController.popBackStack()
            }

            btnReadBook.setOnClickListener {
                val action = BookDetailsFragmentDirections.actionBookDetailsToPdfDetails(url)
                navController.navigate(action)
            }

            ibFavourite.setOnClickListener {
                val currentFavoriteStatus = viewModel.book.value.favourite
                val newFavoriteStatus = !currentFavoriteStatus
                viewModel.updateBookFavourite(newFavoriteStatus)
            }
        }
    }

    override fun setupViewModelObserver() {
        super.setupViewModelObserver()

        lifecycleScope.launch {
            viewModel.book.collect{
                binding.run {
                    tvBookTitle.text = it.title
                    tvDesc.text = it.desc
                    tvCategory.text = it.category
                    tvDate.text = formatTimestamp(it.timestamp)
                    url = it.url

                    if (it.favourite){
                        ibFavourite.setImageResource(R.drawable.ic_favorite)
                    } else {
                        ibFavourite.setImageResource(R.drawable.ic_favorite_border)
                    }

                    loadPdf(it, pdfView, tvSize, progressBar, tvPage)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.success.collect{
                viewModel.getBook(args.bookId2)
            }
        }
    }

}