package com.renbin.bookproject.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.renbin.bookproject.R
import com.renbin.bookproject.databinding.FragmentProfileBinding
import com.renbin.bookproject.ui.base.BaseFragment
import com.renbin.bookproject.core.util.Utility.formatTimestamp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.bumptech.glide.Glide
import com.renbin.bookproject.core.util.Utility.showToast
import com.renbin.bookproject.data.model.Book
import com.renbin.bookproject.ui.adapter.FavouriteBookAdapter

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    override val viewModel: ProfileViewModel by viewModels()
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var adapter: FavouriteBookAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onFragmentResult() {
        super.onFragmentResult()
        // Register for activity result using ActivityResultContracts.PickVisualMedia
        pickMedia = registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()) { uri ->
            // Check if a valid URI is returned
            if (uri != null) {
                // If URI is not null, update the profile picture using the ViewModel
                viewModel.updateProfilePic(uri)
            } else {
                showToast(requireContext(),"No media selected", R.drawable.ic_image)
            }
        }
    }

    override fun setupUIComponents() {
        super.setupUIComponents()
        setupAdapter()

        binding.run {
            ibBack.setOnClickListener {
                navController.popBackStack()
            }

            ibEdit.setOnClickListener {
                // Show/hide UI components for editing profile
                ibEdit.visibility = View.GONE
                ibConfirm.visibility = View.VISIBLE
                ibEditImage.visibility = View.VISIBLE
                etName.visibility = View.VISIBLE
                tvTitle.text = getString(R.string.edit_profile)
            }

            ibConfirm.setOnClickListener {
                // Show/hide UI components after confirming profile edit
                ibEdit.visibility = View.VISIBLE
                ibConfirm.visibility = View.GONE
                ibEditImage.visibility = View.GONE
                etName.visibility = View.GONE
                tvTitle.text = getString(R.string.profile)

                // Update user's name and refresh user data
                val name = etName.text.toString()
                viewModel.updateName(name)
                viewModel.getCurrentUser()
            }

            ibEditImage.setOnClickListener {
                // Launch the media picker when editing profile picture
                pickMedia.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        }
    }

    override fun setupViewModelObserver() {
        super.setupViewModelObserver()

        lifecycleScope.launch{
            // Observe user data changes
            viewModel.user.collect{
                binding.run {
                    tvName.text = it.name
                    tvEmail.text = it.email
                    tvCreatedAt.text = formatTimestamp(it.timestamp)
                    etName.setText(it.name)
                }
            }
        }

        lifecycleScope.launch {
            // Observe user's favorite books data changes
            viewModel.favourites.collect{
                binding.tvFavourite.text = it.toString()
            }
        }

        lifecycleScope.launch {
            // Observe user's profile picture URI changes
            viewModel.profileUri.collect{
                Glide.with(binding.root)
                    .load(it)
                    .placeholder(R.drawable.ic_person)
                    .into(binding.ivProfile)
            }
        }

        lifecycleScope.launch {
            // Observe user's favorite books list changes
            viewModel.books.collect{
                adapter.setFavouriteBooks(it)
            }
        }

        lifecycleScope.launch {
            // Observe loading state and update UI accordingly
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

    // Setup the adapter for the RecyclerView
    private fun setupAdapter(){
        adapter = FavouriteBookAdapter(emptyList())
        adapter.listener = object: FavouriteBookAdapter.Listener{
            override fun onClick(book: Book) {
                // Navigate to the details screen when a book is clicked
                val action = ProfileFragmentDirections.actionGlobalBookDetails(book.id)
                navController.navigate(action)
            }

            override fun onItemClick(book: Book) {
                // Toggle the favorite status of a book and refresh user data
                val currentFavoriteStatus = book.favourite
                val newFavoriteStatus = !currentFavoriteStatus
                Log.d("debugging", newFavoriteStatus.toString())
                viewModel.updateFavourite(newFavoriteStatus, book.id)
                viewModel.getCurrentUser()
            }
        }
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvBook.adapter = adapter
        binding.rvBook.layoutManager = layoutManager
    }
}