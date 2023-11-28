package com.renbin.bookproject.ui.pdf.viewModel

import androidx.lifecycle.viewModelScope
import com.renbin.bookproject.core.service.AuthService
import com.renbin.bookproject.data.repo.CategoryRepo
import com.renbin.bookproject.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Base class for ViewModel logic related to adding/editing PDF
abstract class BaseAddEditPdfViewModel(
    private val categoryRepo: CategoryRepo,
    private val authService: AuthService
) : BaseViewModel() {
    // StateFlow to hold the list of category names
    private val _categoryName = MutableStateFlow<List<String>>(emptyList())
    val categoryName: StateFlow<List<String>> = _categoryName

    // Initialization block to fetch all categories on ViewModel creation
    override fun onCreate() {
        super.onCreate()
        getAllCategories()
    }

    // Function to fetch all categories from the repository
    private fun getAllCategories() {
        val user = authService.getCurrentUser()
        user?.let { currentUser ->
            viewModelScope.launch(Dispatchers.IO) {
                safeApiCall {
                    categoryRepo.getAllCategoryNames(currentUser.uid)
                }?.collect {
                    _categoryName.value = it
                }
            }
        }
    }
}