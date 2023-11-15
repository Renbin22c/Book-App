package com.renbin.bookproject.ui.book.viewModel

import androidx.lifecycle.viewModelScope
import com.renbin.bookproject.core.service.AuthService
import com.renbin.bookproject.data.model.Book
import com.renbin.bookproject.data.repo.BookRepo
import com.renbin.bookproject.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val repo: BookRepo,
    private val authService: AuthService
): BaseViewModel() {
    private val _books: MutableStateFlow<List<Book>> = MutableStateFlow(emptyList())
    val books: StateFlow<List<Book>> = _books


    fun getBookByCategory(category: String){
        val user = authService.getCurrentUser()
        user?.let {currentUser ->
            viewModelScope.launch(Dispatchers.IO) {
                safeApiCall {
                    repo.getBookByCategory(category, currentUser.uid)
                }?.collect{
                    _books.value = it
                }
            }
        }
    }

    fun deleteBook(id: String){
        viewModelScope.launch(Dispatchers.IO) {
            repo.delete(id)
            _success.emit("Delete Book Successfully !")
        }
    }

    fun filterBooksByQuery(query: String): List<Book> {
        return _books.value.filter { book ->
            book.title.contains(query, ignoreCase = true)
        }
    }
}