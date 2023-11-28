package com.renbin.bookproject.ui.book.viewModel

import androidx.lifecycle.viewModelScope
import com.renbin.bookproject.core.service.AuthService
import com.renbin.bookproject.data.model.Book
import com.renbin.bookproject.data.model.RecycleBook
import com.renbin.bookproject.data.repo.BookRepo
import com.renbin.bookproject.data.repo.RecycleBookRepo
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
    private val recycleBookRepo: RecycleBookRepo,
    private val authService: AuthService
): BaseViewModel() {
    // MutableStateFlow representing the current list of books
    private val _books: MutableStateFlow<List<Book>> = MutableStateFlow(emptyList())
    val books: StateFlow<List<Book>> = _books

    // Retrieves books by category for the current user
    fun getBookByCategory(category: String){
        val user = authService.getCurrentUser()
        user?.let {currentUser ->
            viewModelScope.launch(Dispatchers.IO) {
                _loading.emit(true)
                safeApiCall {
                    repo.getBookByCategory(category, currentUser.uid)
                }?.collect{
                    _books.value = it
                    _loading.emit(false)
                }
            }
        }
    }

    // Deletes a book by its ID
    fun deleteBook(id: String){
        viewModelScope.launch(Dispatchers.IO) {
            repo.delete(id)
            _success.emit("Delete Book Successfully !")
        }
    }

    // Adds a book to the recycle bin
    fun addRecycleBook(title: String, desc: String, category: String,
                       link: String, url: String, uid:String, timeStamp: Long){
        val recycleBook = RecycleBook(
            title = title, desc = desc, category = category, link = link,
            url = url, uid = uid, timestamp = timeStamp)
        viewModelScope.launch(Dispatchers.IO) {
            safeApiCall {
                recycleBookRepo.insert(recycleBook)
            }
        }
    }

    // Filters books based on a query string
    fun filterBooksByQuery(query: String): List<Book> {
        return _books.value.filter { book ->
            book.title.contains(query, ignoreCase = true)
        }
    }
}