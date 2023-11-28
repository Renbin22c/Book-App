package com.renbin.bookproject.ui.recycleBook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.renbin.bookproject.R
import com.renbin.bookproject.data.model.Book
import com.renbin.bookproject.data.model.RecycleBook
import com.renbin.bookproject.databinding.FragmentRecycleBookBinding
import com.renbin.bookproject.ui.adapter.BookAdapter
import com.renbin.bookproject.ui.adapter.RecycleBookAdapter
import com.renbin.bookproject.ui.base.BaseFragment
import com.renbin.bookproject.ui.book.BookFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecycleBookFragment : BaseFragment<FragmentRecycleBookBinding>() {
    override val viewModel: RecycleBookViewModel by viewModels()
    private lateinit var adapter: RecycleBookAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRecycleBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupUIComponents() {
        super.setupUIComponents()
        setupAdapter()

        binding.run {
            ibBack.setOnClickListener {
                navController.popBackStack()
            }
        }
    }

    override fun setupViewModelObserver() {
        super.setupViewModelObserver()

        lifecycleScope.launch {
            // Observe recycle books data changes
            viewModel.recycleBooks.collect{
                adapter.setRecycleBooks(it)
            }
        }

        lifecycleScope.launch {
            // Observe loading state and update UI accordingly
            viewModel.loading.collect{
                if (it){
                    binding.progressBar.visibility = View.VISIBLE
                }else{
                    binding.progressBar.visibility = View.GONE
                    if (adapter.itemCount == 0) binding.tvEmpty.visibility = View.VISIBLE
                }
            }
        }
    }

    // Setup the adapter for the RecyclerView
    private fun setupAdapter(){
        adapter = RecycleBookAdapter(emptyList())
        adapter.listener = object: RecycleBookAdapter.Listener{

            override fun onItemClick(view: View, recycleBook: RecycleBook) {
                // Show the action menu when a recycle book item is clicked
                showActionMenu(view, recycleBook)
            }
        }
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvBook.adapter = adapter
        binding.rvBook.layoutManager = layoutManager
    }

    // Show the action menu for recycle book items
    private fun showActionMenu(view:View, recycleBook: RecycleBook){
        val popupMenu = PopupMenu(requireContext(),view)
        popupMenu.inflate(R.menu.recycle_book_item_action_menu)
        popupMenu.setForceShowIcon(true)
        popupMenu.show()

        // Set click listener for menu items
        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.deleteBook -> {
                    // Delete the recycle book and its associated storage book
                    viewModel.deleteRecycleBook(recycleBook.id)
                    viewModel.deleteStorageBook(recycleBook.url)
                    true
                }

                else ->{
                    // Add the book back and delete it from the recycle bin
                    viewModel.addBackBook(
                        recycleBook.title, recycleBook.desc, recycleBook.category, recycleBook.link,
                        recycleBook.url, recycleBook.uid, recycleBook.timestamp)
                    viewModel.deleteRecycleBook(recycleBook.id)
                    true
                }
            }
        }
    }
}