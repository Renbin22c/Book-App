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

        // Triggering the ViewModel to fetch book details based on the provided book ID
        viewModel.getBook(args.bookId2)

        binding.run {
            // Back button click listener to navigate back in the navigation stack
            ibBack.setOnClickListener {
                navController.popBackStack()
            }

            // "Read Book" button click listener to navigate to the PDF details screen
            btnReadBook.setOnClickListener {
                val action = BookDetailsFragmentDirections.actionBookDetailsToPdfDetails(url)
                navController.navigate(action)
            }

            // Favorite button click listener to toggle the favorite status of the boo
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
            // Observe changes in the book data
            viewModel.book.collect{
                binding.run {
                    tvBookTitle.text = it.title
                    tvDesc.text = it.desc
                    tvCategory.text = it.category
                    tvDate.text = formatTimestamp(it.timestamp)
                    url = it.url

                    // Set the favorite button image based on the favorite status
                    if (it.favourite){
                        ibFavourite.setImageResource(R.drawable.ic_favorite)
                    } else {
                        ibFavourite.setImageResource(R.drawable.ic_favorite_border)
                    }

                    // Load PDF details into the view
                    loadPdf(it, pdfView, tvSize, progressBar, tvPage)
                }
            }
        }

        lifecycleScope.launch {
            // Observe success events and refresh the book data
            viewModel.success.collect{
                viewModel.getBook(args.bookId2)
            }
        }
    }

}