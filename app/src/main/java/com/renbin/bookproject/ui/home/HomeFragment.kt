package com.renbin.bookproject.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
    // Initialize the ViewModel using Hilt
    override val viewModel: HomeViewModel by viewModels()

    // Initialize adapters for categories and books
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var bookAdapter: BookAdapter

    // Variable to track the folder state
    private var folder = true

    // Variable to track the folder state
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

        // Setup adapters for categories and books
        setupCategoryAdapter()
        setupBookAdapter()

        // Setup search functionality
        search()

        binding.run {
            // Set click listener for adding a new category
            btnCategory.setOnClickListener {
                val dialogFragment = AddCategoryPopUpFragment()
                dialogFragment.show(childFragmentManager, "AddCategoryPopUpFragment")
            }

            // Set click listener for adding a new PDF
            fabPDF.setOnClickListener {
                if (categoryAdapter.itemCount > 0) {
                    val action = HomeFragmentDirections.actionHomeToAddPdf()
                    navController.navigate(action)
                } else {
                    showToast(requireContext(),"Please add a category first", R.drawable.ic_pdf)
                }
            }

            // Set click listener for toggling between folder and book view
            ibFolder.setOnClickListener {
                folder = !folder
                viewModel.setFolderState(folder)
            }

            // Set click listener for profile menu
            ibProfile.setOnClickListener {
                showActionProfileMenu(it)
            }

        }
    }

    override fun setupViewModelObserver() {
        super.setupViewModelObserver()

        lifecycleScope.launch {
            // Observe categories data changes
            viewModel.categories.collect {
                categoryAdapter.setCategories(it)
                if (categoryAdapter.itemCount > 0)
                    binding.tvEmpty.visibility = View.GONE
            }
        }

        lifecycleScope.launch {
            // Observe books data changes
            viewModel.books.collect{
                bookAdapter.setBooks(it)
            }
        }

        lifecycleScope.launch {
            // Observe folder state changes
            viewModel.folderState.observe(viewLifecycleOwner) { isFolderVisible ->
                folder = isFolderVisible
                updateUIBasedOnFolderState()
            }
        }

        lifecycleScope.launch {
            // Observe loading state changes
            viewModel.loading.collect{
                if (it){
                    binding.progressBar.visibility = View.VISIBLE
                } else{
                    binding.progressBar.visibility = View.GONE
                    if(folder){
                        if(categoryAdapter.itemCount == 0)
                            binding.tvEmpty.visibility = View.VISIBLE
                        else binding.tvEmpty.visibility = View.GONE
                    }else{
                        if(bookAdapter.itemCount == 0)
                            binding.tvEmpty.visibility = View.VISIBLE
                        else binding.tvEmpty.visibility = View.GONE
                    }
                }
            }
        }
    }

    // Setup adapter for categories
    private fun setupCategoryAdapter(){
        categoryAdapter = CategoryAdapter(emptyList())
        categoryAdapter.listener = object: CategoryAdapter.Listener{
            override fun onClick(category: Category) {
                // Navigate to the books under the selected category
                val action = HomeFragmentDirections.actionHomeToBook(category.category)
                navController.navigate(action)
            }

            override fun onDelete(category: Category) {
                // Delete the selected category
                viewModel.delete(category)
            }

        }
        val layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        binding.rvCategory.adapter = categoryAdapter
        binding.rvCategory.layoutManager = layoutManager
    }

    // Setup adapter for books
    private fun setupBookAdapter(){
        bookAdapter = BookAdapter(emptyList())
        bookAdapter.listener = object: BookAdapter.Listener{
            override fun onClick(book: Book) {
                // Navigate to the details of the selected book
                val action = HomeFragmentDirections.actionGlobalBookDetails(book.id)
                navController.navigate(action)
            }

            override fun onItemClick(view: View, book: Book) {
                // Show action menu for the selected book
                showActionBookMenu(view, book)
            }
        }
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvBook.adapter = bookAdapter
        binding.rvBook.layoutManager = layoutManager
    }

    // Update UI based on folder state
    private fun updateUIBasedOnFolderState() {
        if (folder) {
            binding.rvCategory.visibility = View.VISIBLE
            binding.llBook.visibility = View.GONE
            binding.ibFolder.setImageResource(R.drawable.ic_folder)
            binding.tvEmpty.text = getString(R.string.your_category_is_empty)
            if (categoryAdapter.itemCount > 0)
                binding.tvEmpty.visibility = View.GONE
        } else {
            binding.rvCategory.visibility = View.GONE
            binding.llBook.visibility = View.VISIBLE
            binding.ibFolder.setImageResource(R.drawable.ic_folder_off)
            binding.tvEmpty.text = getString(R.string.your_book_is_empty)
            if (bookAdapter.itemCount == 0)
                binding.tvEmpty.visibility = View.VISIBLE
            else
                binding.tvEmpty.visibility = View.GONE
        }
    }

    // Show the profile action menu
    private fun showActionProfileMenu(view:View){
        val popupMenu = PopupMenu(requireContext(),view)
        popupMenu.inflate(R.menu.profile_item_action_menu)
        popupMenu.setForceShowIcon(true)
        popupMenu.show()


        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.profile ->{
                    // Navigate to the profile screen
                    val action = HomeFragmentDirections.actionHomeToProfile()
                    navController.navigate(action)
                    true
                }
                R.id.recycleBin->{
                    // Navigate to the recycle bin screen
                    val action = HomeFragmentDirections.actionHomeToRecycleBook()
                    navController.navigate(action)
                    true
                }
                else -> {
                    // Show logout confirmation dialog
                    alertLogout()
                    true
                }
            }
        }
    }

    // Show the action menu for the selected book
    private fun showActionBookMenu(view:View, book: Book){
        val popupMenu = PopupMenu(requireContext(),view)
        popupMenu.inflate(R.menu.book_item_action_menu)
        popupMenu.setForceShowIcon(true)
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.deleteBook -> {
                    // Move the book to the recycle bin and delete it
                    viewModel.addRecycleBook(
                        book.title, book.desc, book.category, book.link,
                        book.url, book.uid, book.timestamp
                    )
                    viewModel.deleteBook(book.id)
                    true
                }

                else ->{
                    // Navigate to the edit PDF screen
                    val action = HomeFragmentDirections.actionGlobalEditPdf(book.id)
                    navController.navigate(action)
                    true
                }
            }
        }
    }

    // Setup search functionality
    private fun search(){
        searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    // Filter books based on the query and set them in the adapter
                    val filteredBooks = viewModel.filterBooksByQuery(query)
                    bookAdapter.setBooks(filteredBooks)
                }
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    // Filter books based on the new text and set them in the adapter
                    val filteredBooks = viewModel.filterBooksByQuery(newText)
                    bookAdapter.setBooks(filteredBooks)
                } else{
                    bookAdapter.setBooks(emptyList())
                }
                return true
            }
        })
    }

    // Show a confirmation dialog for logout
    private fun alertLogout(){
        val dialogView = layoutInflater.inflate(R.layout.alert_dialog, null)
        val alertDialog = MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btnConfirm)

        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            // Logout the user and navigate to the main screen
            viewModel.logout()
            val action = HomeFragmentDirections.actionHomeToMain()
            navController.navigate(action)
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
}