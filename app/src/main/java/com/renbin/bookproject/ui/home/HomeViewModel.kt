package com.renbin.bookproject.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.renbin.bookproject.core.service.AuthService
import com.renbin.bookproject.data.model.Book
import com.renbin.bookproject.data.model.Category
import com.renbin.bookproject.data.repo.BookRepo
import com.renbin.bookproject.data.repo.CategoryRepo
import com.renbin.bookproject.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val categoryRepo: CategoryRepo,
    private val authService: AuthService,
    private val bookRepo: BookRepo
) : BaseViewModel() {
    private val _categories: MutableStateFlow<List<Category>> = MutableStateFlow(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _books: MutableStateFlow<List<Book>> = MutableStateFlow(emptyList())
    val books: StateFlow<List<Book>> = _books

    private val _folderState: MutableLiveData<Boolean> = MutableLiveData(true)
    val folderState: LiveData<Boolean> = _folderState

    private val _loading: MutableSharedFlow<Boolean>  = MutableSharedFlow()
    val loading: SharedFlow<Boolean> = _loading

    private val user = authService.getCurrentUser()

    init {
        getAllCategories()
        getAllBooks()
    }

    private fun getAllCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.emit(true)
            user?.let { currentUser ->
                safeApiCall {
                    categoryRepo.getAllCategories(currentUser.uid)
                }?.collect{
                    _categories.value = it
                }
            }
            _loading.emit(false)
        }
    }

    private fun getAllBooks() {
        user?.let { currentUser ->
            viewModelScope.launch(Dispatchers.IO) {
                user.let { currentUser ->
                    safeApiCall {
                        bookRepo.getAllBooks(currentUser.uid)
                    }?.collect {
                        _books.value = it
                    }
                }
            }
        }
    }

    fun delete(category: Category){
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepo.delete(category.id)
            _success.emit("Delete Category Successfully !")
        }
    }

    fun deleteBook(id: String){
        viewModelScope.launch(Dispatchers.IO) {
            bookRepo.delete(id)
            _success.emit("Delete Book Successfully !")
        }
    }

    fun setFolderState(isFolderVisible: Boolean) {
        _folderState.value = isFolderVisible
    }

    fun filterBooksByQuery(query: String): List<Book> {
        return _books.value.filter { book ->
            book.title.contains(query, ignoreCase = true)
        }
    }

    fun logout(){
        authService.logout()
        viewModelScope.launch(Dispatchers.IO) {
            _success.emit("Logout Successfully !")
        }
    }

}