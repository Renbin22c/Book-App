package com.renbin.bookproject.ui.pdf.viewModel

import androidx.lifecycle.viewModelScope
import com.renbin.bookproject.core.service.AuthService
import com.renbin.bookproject.data.repo.CategoryRepo
import com.renbin.bookproject.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseAddEditPdfViewModel(
    private val categoryRepo: CategoryRepo,
    private val authService: AuthService
) : BaseViewModel() {
    private val _categoryName = MutableStateFlow<List<String>>(emptyList())
    val categoryName: StateFlow<List<String>> = _categoryName

    init {
        getAllCategories()
    }

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

    protected fun validate(title: String, desc: String, category: String): String?{
        return if (title.isEmpty()){
            "Title cannot be empty !!!"
        } else if(desc.isEmpty()){
            "Description cannot be empty !!!"
        } else if(category.isEmpty()){
            "Category cannot be empty !!!"
        } else{
            null
        }
    }
}