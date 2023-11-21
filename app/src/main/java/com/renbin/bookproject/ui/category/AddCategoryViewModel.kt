package com.renbin.bookproject.ui.category

import androidx.lifecycle.viewModelScope
import com.renbin.bookproject.core.service.AuthService
import com.renbin.bookproject.data.model.Category
import com.renbin.bookproject.data.repo.CategoryRepo
import com.renbin.bookproject.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCategoryViewModel @Inject constructor(
    private val repo: CategoryRepo,
    private val authService: AuthService
): BaseViewModel() {

    fun submit(category: String){
        val user = authService.getCurrentUser()
        user?.let { currentUser ->
            val category = Category(category = category)
            viewModelScope.launch(Dispatchers.IO) {
                safeApiCall { repo.insert(category, currentUser.uid) }
                _success.emit("Add Category Successfully!!!")
            }
        }
    }
}