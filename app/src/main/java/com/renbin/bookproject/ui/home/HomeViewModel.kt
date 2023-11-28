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
    private val recycleBookRepo: RecycleBookRepo
) : BaseViewModel() {

    // StateFlow to observe the list of categories
    private val _categories: MutableStateFlow<List<Category>> = MutableStateFlow(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    // StateFlow to observe the list of books
    private val _books: MutableStateFlow<List<Book>> = MutableStateFlow(emptyList())
    val books: StateFlow<List<Book>> = _books

    // LiveData to observe the folder visibility state
    private val _folderState: MutableLiveData<Boolean> = MutableLiveData(true)
    val folderState: LiveData<Boolean> = _folderState

    // Get the current user using AuthService
    private val user = authService.getCurrentUser()

    override fun onCreate() {
        super.onCreate()
        // Fetch all categories and books when the ViewModel is created
        getAllCategories()
        getAllBooks()
    }

    // Function to get all categories for the current user
    private fun getAllCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            // Emit loading state
            _loading.emit(true)
            // Check if the user is not null
            user?.let { currentUser ->
                // Call the repository to get all categories for the user
                safeApiCall {
                    categoryRepo.getAllCategories(currentUser.uid)
                }?.collect{
                    // Update the StateFlow with the fetched categories
                    _categories.value = it
                    // Emit loading state
                    _loading.emit(false)
                }
            }
        }
    }

    // Function to get all books for the current user
    private fun getAllBooks() {
        viewModelScope.launch(Dispatchers.IO) {
            // Emit loading state
            _loading.emit(true)
            // Check if the user is not null
            user?.let { currentUser ->
                // Call the repository to get all books for the user
                safeApiCall {
                    bookRepo.getAllBooks(currentUser.uid)
                }?.collect {
                    // Update the StateFlow with the fetched books
                    _books.value = it
                    // Emit loading state
                    _loading.emit(false)
                }
            }
        }
    }

    // Function to delete a category
    fun delete(category: Category){
        viewModelScope.launch(Dispatchers.IO) {
            // Call the repository to delete the category
            categoryRepo.delete(category.id)
            // Emit success message
            _success.emit("Delete Category Successfully !")
        }
    }

    // Function to delete a book
    fun deleteBook(id: String){
        viewModelScope.launch(Dispatchers.IO) {
            // Call the repository to delete the book
            bookRepo.delete(id)
            // Emit success message
            _success.emit("Delete Book Successfully !")
        }
    }

    // Function to add a book to the recycle bin
    fun addRecycleBook(title: String, desc: String, category: String,
        link: String, url: String, uid:String, timeStamp: Long){
        // Create a RecycleBook object
        val recycleBook = RecycleBook(
            title = title, desc = desc, category = category, link = link,
            url = url, uid = uid, timestamp = timeStamp)
        viewModelScope.launch(Dispatchers.IO) {
            // Insert the recycle book using the repository
            safeApiCall {
                recycleBookRepo.insert(recycleBook)
            }
        }
    }

    // Function to set the folder visibility state
    fun setFolderState(isFolderVisible: Boolean) {
        _folderState.value = isFolderVisible
    }

    // Function to filter books by query
    fun filterBooksByQuery(query: String): List<Book> {
        return _books.value.filter { book ->
            book.title.contains(query, ignoreCase = true)
        }
    }

    // Function to perform logout
    fun logout(){
        authService.logout()
        viewModelScope.launch(Dispatchers.IO) {
            _success.emit("Logout Successfully !")
        }
    }

}