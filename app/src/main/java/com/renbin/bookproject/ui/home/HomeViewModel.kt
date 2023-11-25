package com.renbin.bookproject.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.renbin.bookproject.core.service.AuthService
import com.renbin.bookproject.core.service.StorageService
import com.renbin.bookproject.data.model.Book
import com.renbin.bookproject.data.model.Category
import com.renbin.bookproject.data.model.RecycleBook
import com.renbin.bookproject.data.repo.BookRepo
import com.renbin.bookproject.data.repo.CategoryRepo
import com.renbin.bookproject.data.repo.RecycleBookRepo
import com.renbin.bookproject.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val categoryRepo: CategoryRepo,
    private val authService: AuthService,
    private val bookRepo: BookRepo,
    private val recycleBookRepo: RecycleBookRepo,
    private val storage: StorageService
) : BaseViewModel() {
    private val _categories: MutableStateFlow<List<Category>> = MutableStateFlow(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _books: MutableStateFlow<List<Book>> = MutableStateFlow(emptyList())
    val books: StateFlow<List<Book>> = _books

    private val _folderState: MutableLiveData<Boolean> = MutableLiveData(true)
    val folderState: LiveData<Boolean> = _folderState

    private val user = authService.getCurrentUser()

    override fun onCreate() {
        super.onCreate()
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
                    _loading.emit(false)
                }
            }
        }
    }

    private fun getAllBooks() {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.emit(true)
            user?.let { currentUser ->
                safeApiCall {
                    bookRepo.getAllBooks(currentUser.uid)
                }?.collect {
                    _books.value = it
                    _loading.emit(false)
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