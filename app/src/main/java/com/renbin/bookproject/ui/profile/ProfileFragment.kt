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
import com.renbin.bookproject.data.model.Book
import com.renbin.bookproject.ui.adapter.FavouriteBookAdapter

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    override val viewModel: ProfileViewModel by viewModels()
    private var editMode: Boolean = false
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var adapter: FavouriteBookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pickMedia = registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                viewModel.updateProfilePic(uri)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupUIComponents() {
        super.setupUIComponents()
        setupAdapter()

        binding.run {
            ibBack.setOnClickListener {
                navController.popBackStack()
            }

            ibEdit.setOnClickListener {
                if(editMode){
                    tvTitle.text = getString(R.string.profile)
                    ibEdit.setImageResource(R.drawable.ic_edit)
                    ibEditImage.visibility = View.GONE
                    etName.visibility = View.GONE

                    val name = etName.text.toString()
                    viewModel.updateName(name)
                    viewModel.getCurrentUser()

                    editMode = false
                } else {
                    tvTitle.text = getString(R.string.edit_profile)
                    ibEdit.setImageResource(R.drawable.ic_check)
                    ibEditImage.visibility = View.VISIBLE
                    etName.visibility = View.VISIBLE

                    editMode = true
                }
            }

            ibEditImage.setOnClickListener {
                pickMedia.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        }
    }

    override fun setupViewModelObserver() {
        super.setupViewModelObserver()

        lifecycleScope.launch{
            viewModel.user.collect{
                binding.run {
                    tvName.text = it.name
                    tvEmail.text = it.email
                    tvCreatedAt.text = formatTimestamp(it.timestamp)
                    tvFavourite.text = it.favourites

                    etName.setText(it.name)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.profileUri.collect{
                Glide.with(binding.root)
                    .load(it)
                    .placeholder(R.drawable.ic_person)
                    .into(binding.ivProfile)
            }
        }

        lifecycleScope.launch {
            viewModel.books.collect{
                adapter.setFavouriteBooks(it)
            }
        }

        lifecycleScope.launch {
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

    private fun setupAdapter(){
        adapter = FavouriteBookAdapter(emptyList())
        adapter.listener = object: FavouriteBookAdapter.Listener{
            override fun onClick(book: Book) {
                val action = ProfileFragmentDirections.actionGlobalBookDetails(book.id)
                navController.navigate(action)
            }

            override fun onItemClick(book: Book) {
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