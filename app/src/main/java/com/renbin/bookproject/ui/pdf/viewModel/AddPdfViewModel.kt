package com.renbin.bookproject.ui.pdf.viewModel

import androidx.lifecycle.viewModelScope
import com.renbin.bookproject.core.service.AuthService
import com.renbin.bookproject.data.model.Book
import com.renbin.bookproject.data.repo.BookRepo
import com.renbin.bookproject.data.repo.CategoryRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPdfViewModel @Inject constructor(
    categoryRepo: CategoryRepo,
    authService: AuthService,
    private val bookRepo: BookRepo,
): BaseAddEditPdfViewModel(categoryRepo, authService) {
    val user = authService.getCurrentUser()

    // Function to submit a new book to the repository
    fun submit(title: String, desc: String, category: String, url: String, link: String) {
        val book = Book(title = title, desc= desc, category = category, url = url, link = link)
        viewModelScope.launch(Dispatchers.IO) {
            user?.let {currentUser ->
                safeApiCall { bookRepo.insert(book, currentUser.uid) }
                _success.emit("Add Book Successfully !")
            }
        }
    }

}