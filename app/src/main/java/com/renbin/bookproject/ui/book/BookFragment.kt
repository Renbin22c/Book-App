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
        setupAdapter()
        search()

        viewModel.getBookByCategory(args.categoryName)

        binding.run {
            ibBack.setOnClickListener {
                navController.popBackStack()
            }

            tvCategoryName.text = args.categoryName
        }
    }

    override fun setupViewModelObserver() {
        super.setupViewModelObserver()

        lifecycleScope.launch {
            viewModel.books.collect{
                adapter.setBooks(it)
                if (adapter.itemCount == 0){
                    binding.tvEmpty.visibility = View.VISIBLE
                } else{
                    binding.tvEmpty.visibility = View.GONE
                }
            }
        }
    }

    private fun setupAdapter(){
        adapter = BookAdapter(emptyList())
        adapter.listener = object: BookAdapter.Listener{
            override fun onClick(book: Book) {
                val action = BookFragmentDirections.actionGlobalBookDetails(book.id)
                navController.navigate(action)
            }

            override fun onItemClick(view: View, book: Book) {
                showActionMenu(view, book)
            }
        }
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvBook.adapter = adapter
        binding.rvBook.layoutManager = layoutManager
    }

    private fun search(){
        searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    val filteredBooks = viewModel.filterBooksByQuery(query)
                    adapter.setBooks(filteredBooks)
                }
                searchView.clearFocus()
                return true
            }

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

    private fun showActionMenu(view:View, book: Book){
        val popupMenu = PopupMenu(requireContext(),view)
        popupMenu.inflate(R.menu.book_item_action_menu)
        popupMenu.setForceShowIcon(true)
        popupMenu.show()


        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.deleteBook -> {
                    viewModel.deleteBook(book.id)
                    true
                }

                else ->{
                    val action = BookFragmentDirections.actionGlobalEditPdf(book.id)
                    navController.navigate(action)
                    true
                }
            }
        }
    }
}