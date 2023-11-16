package com.renbin.bookproject.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.renbin.bookproject.R
import com.renbin.bookproject.data.model.Book
import com.renbin.bookproject.data.model.Category
import com.renbin.bookproject.databinding.FragmentHomeBinding
import com.renbin.bookproject.ui.adapter.BookAdapter
import com.renbin.bookproject.ui.adapter.CategoryAdapter
import com.renbin.bookproject.ui.base.BaseFragment
import com.renbin.bookproject.ui.category.AddCategoryPopUpFragment
import com.renbin.bookproject.core.util.Utility.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override val viewModel: HomeViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var bookAdapter: BookAdapter
    private var folder = true
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun setupUIComponents() {
        super.setupUIComponents()
        setupCategoryAdapter()
        setupBookAdapter()
        search()

        binding.run {
            btnCategory.setOnClickListener {
                val dialogFragment = AddCategoryPopUpFragment()
                // Show the dialog fragment using childFragmentManager
                dialogFragment.show(childFragmentManager, "AddCategoryPopUpFragment")
            }

            fabPDF.setOnClickListener {
                if (categoryAdapter.itemCount > 0) {
                    val action = HomeFragmentDirections.actionHomeToAddPdf()
                    navController.navigate(action)
                } else {
                    showToast(requireContext(),"Please add a category first")
                }
            }

            ibFolder.setOnClickListener {
                folder = !folder
                viewModel.setFolderState(folder)
            }

            ibProfile.setOnClickListener {
                showActionProfileMenu(it)
            }

        }
    }

    override fun setupViewModelObserver() {
        super.setupViewModelObserver()

        lifecycleScope.launch {
            viewModel.categories.collect { categories ->
                val sortedCategories = categories.sortedBy { it.timestamp }
                categoryAdapter.setCategories(sortedCategories)
                empty()
            }
        }

        lifecycleScope.launch {
            viewModel.books.collect{
                bookAdapter.setBooks(it)
                empty()
            }
        }

        lifecycleScope.launch {
            viewModel.folderState.observe(viewLifecycleOwner) { isFolderVisible ->
                folder = isFolderVisible
                updateUIBasedOnFolderState()
            }
        }
    }

    private fun setupCategoryAdapter(){
        categoryAdapter = CategoryAdapter(emptyList())
        categoryAdapter.listener = object: CategoryAdapter.Listener{
            override fun onClick(category: Category) {
                val action = HomeFragmentDirections.actionHomeToBook(category.category)
                navController.navigate(action)
            }

            override fun onDelete(category: Category) {
                viewModel.delete(category)
            }

        }
        val layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        binding.rvCategory.adapter = categoryAdapter
        binding.rvCategory.layoutManager = layoutManager
    }

    private fun setupBookAdapter(){
        bookAdapter = BookAdapter(emptyList())
        bookAdapter.listener = object: BookAdapter.Listener{
            override fun onClick(book: Book) {
                val action = HomeFragmentDirections.actionGlobalBookDetails(book.id)
                navController.navigate(action)
            }

            override fun onItemClick(view: View, book: Book) {
                showActionBookMenu(view, book)
            }
        }
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvBook.adapter = bookAdapter
        binding.rvBook.layoutManager = layoutManager
    }

    private fun updateUIBasedOnFolderState() {
        if (folder) {
            binding.rvCategory.visibility = View.VISIBLE
            binding.llBook.visibility = View.GONE
            binding.ibFolder.setImageResource(R.drawable.ic_folder)
            empty()
        } else {
            binding.rvCategory.visibility = View.GONE
            binding.llBook.visibility = View.VISIBLE
            binding.ibFolder.setImageResource(R.drawable.ic_folder_off)
            empty()
        }
    }

    private fun empty(){
        if(folder){
            if (categoryAdapter.itemCount == 0){
                binding.tvEmpty.text = getString(R.string.your_category_is_empty)
                binding.tvEmpty.visibility = View.VISIBLE
            } else {
                binding.tvEmpty.visibility = View.GONE
            }
        } else {
            if (bookAdapter.itemCount == 0){
                binding.tvEmpty.text = getString(R.string.your_book_is_empty)
                binding.tvEmpty.visibility = View.VISIBLE
            } else {
                binding.tvEmpty.visibility = View.GONE
            }
        }
    }

    private fun showActionProfileMenu(view:View){
        val popupMenu = PopupMenu(requireContext(),view)
        popupMenu.inflate(R.menu.profile_item_action_menu)
        popupMenu.setForceShowIcon(true)
        popupMenu.show()


        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.profile ->{
                    val action = HomeFragmentDirections.actionHomeToProfile()
                    navController.navigate(action)
                    true
                }
                else -> {
                    viewModel.logout()
                    val action = HomeFragmentDirections.actionGlobalMain()
                    navController.navigate(action)
                    true
                }
            }
        }
    }

    private fun showActionBookMenu(view:View, book: Book){
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
                    val action = HomeFragmentDirections.actionGlobalEditPdf(book.id)
                    navController.navigate(action)
                    true
                }
            }
        }
    }

    private fun search(){
        searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    val filteredBooks = viewModel.filterBooksByQuery(query)
                    bookAdapter.setBooks(filteredBooks)
                }
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    val filteredBooks = viewModel.filterBooksByQuery(newText)
                    bookAdapter.setBooks(filteredBooks)
                } else{
                    bookAdapter.setBooks(emptyList())
                }
                return true
            }
        })
    }
}