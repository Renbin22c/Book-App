package com.renbin.bookproject.ui.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.renbin.bookproject.R
import com.renbin.bookproject.data.model.Book
import com.renbin.bookproject.databinding.FragmentBookBinding
import com.renbin.bookproject.ui.adapter.BookAdapter
import com.renbin.bookproject.ui.base.BaseFragment
import com.renbin.bookproject.ui.book.viewModel.BookViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookFragment : BaseFragment<FragmentBookBinding>() {
    override val viewModel: BookViewModel by viewModels()
    private val args: BookFragmentArgs by navArgs()
    private lateinit var adapter: BookAdapter
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupUIComponents() {
        super.setupUIComponents()

        // Setting up the RecyclerView adapter and search functionality
        setupAdapter()
        search()

        // Fetching books based on the provided category name
        viewModel.getBookByCategory(args.categoryName)

        binding.run {
            // Back button click listener to navigate back in the navigation stack
            ibBack.setOnClickListener {
                navController.popBackStack()
            }

            // Displaying the category name in the UI
            tvCategoryName.text = args.categoryName
        }
    }

    override fun setupViewModelObserver() {
        super.setupViewModelObserver()

        lifecycleScope.launch {
            // Observing changes in the list of books and updating the adapter
            viewModel.books.collect{
                adapter.setBooks(it)
            }
        }

        lifecycleScope.launch {
            // Observing loading state to show/hide progress bar and empty view
            viewModel.loading.collect{
                if (it){
                    binding.progressBar.visibility = View.VISIBLE
                } else{
                    binding.progressBar.visibility = View.GONE
                    if (adapter.itemCount == 0) binding.tvEmpty.visibility = View.VISIBLE
                }
            }
        }
    }

    // Setting up the RecyclerView adapter, layout manager, and item click listeners
    private fun setupAdapter(){
        adapter = BookAdapter(emptyList())
        adapter.listener = object: BookAdapter.Listener{
            override fun onClick(book: Book) {
                // Navigate to the book details screen on item click
                val action = BookFragmentDirections.actionGlobalBookDetails(book.id)
                navController.navigate(action)
            }

            override fun onItemClick(view: View, book: Book) {
                // Show the action menu on item click
                showActionMenu(view, book)
            }
        }
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvBook.adapter = adapter
        binding.rvBook.layoutManager = layoutManager
    }

    // Setting up the search functionality using the SearchView widget
    private fun search(){
        searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            // Handling the submission of the search query
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    val filteredBooks = viewModel.filterBooksByQuery(query)
                    adapter.setBooks(filteredBooks)
                }
                searchView.clearFocus()
                return true
            }

            // Handling changes in the search query
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    val filteredBooks = viewModel.filterBooksByQuery(newText)
                    adapter.setBooks(filteredBooks)
                } else{
                    adapter.setBooks(emptyList())
                }
                return true
            }
        })
    }

    // Displaying the action menu when clicking on a book item
    private fun showActionMenu(view:View, book: Book){
        val popupMenu = PopupMenu(requireContext(),view)
        popupMenu.inflate(R.menu.book_item_action_menu)
        popupMenu.setForceShowIcon(true)
        popupMenu.show()


        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.deleteBook -> {
                    // Adding the book to the recycle bin and then deleting it
                    viewModel.addRecycleBook(
                        book.title, book.desc, book.category, book.link,
                        book.url, book.uid, book.timestamp
                    )
                    viewModel.deleteBook(book.id)
                    true
                }

                else ->{
                    // Navigating to the global edit PDF screen
                    val action = BookFragmentDirections.actionGlobalEditPdf(book.id)
                    navController.navigate(action)
                    true
                }
            }
        }
    }
}