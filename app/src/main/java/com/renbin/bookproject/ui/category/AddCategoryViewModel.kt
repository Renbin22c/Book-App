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

    // Submit a new category for the current user
    fun submit(category: String){
        // Get the current user
        val user = authService.getCurrentUser()

        // If the user is not null, proceed with submitting the category
        user?.let { currentUser ->
            // Create a Category object
            val categoryObject = Category(category = category)

            // Launch a coroutine in the IO dispatcher to perform the category insertion
            viewModelScope.launch(Dispatchers.IO) {
                // Use safeApiCall to handle errors
                safeApiCall {
                    // Insert the category into the repository, associating it with the current user
                    repo.insert(categoryObject, currentUser.uid)
                }
                // Emit a success message after successful category addition
                _success.emit("Add Category Successfully!!!")
            }
        }
    }
}