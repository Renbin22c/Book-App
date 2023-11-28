package com.renbin.bookproject.ui.recycleBook

import androidx.lifecycle.viewModelScope
import com.renbin.bookproject.core.service.AuthService
import com.renbin.bookproject.core.service.StorageService
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
class RecycleBookViewModel @Inject constructor(
    private val recycleBookRepo: RecycleBookRepo,
    private val bookRepo: BookRepo,
    private val authService: AuthService,
    private val storageService: StorageService
): BaseViewModel(){

    // StateFlow to observe the list of recycle books
    private val _recycleBooks: MutableStateFlow<List<RecycleBook>> = MutableStateFlow(emptyList())
    val recycleBooks: StateFlow<List<RecycleBook>> = _recycleBooks

    // Get the current user using AuthService
    private val user = authService.getCurrentUser()

    override fun onCreate() {
        super.onCreate()
        // Fetch all recycle books when the ViewModel is created
        getAllRecycleBooks()
    }

    // Function to get all recycle books for the current user
    private fun getAllRecycleBooks(){
        viewModelScope.launch(Dispatchers.IO) {
            // Emit loading state
            _loading.emit(true)
            // Check if the user is not null
            user?.let { currentUser ->
                // Call the repository to get all recycle books for the user
                safeApiCall {
                    recycleBookRepo.getAllRecycleBooks(currentUser.uid)
                }?.collect {
                    // Update the StateFlow with the fetched recycle books
                    _recycleBooks.value = it
                    // Emit loading state
                    _loading.emit(false)
                }
            }
        }
    }

    // Function to add a book back from the recycle bin
    fun addBackBook(title: String, desc: String, category: String,
                    link: String, url: String, uid:String, timeStamp: Long){
        // Create a Book object
        val book = Book(
            title = title, desc = desc, category = category, link = link,
            url = url, timestamp = timeStamp)
        viewModelScope.launch(Dispatchers.IO) {
            // Insert the book using the repository
            safeApiCall {
                bookRepo.insert(book, uid)
                // Emit success message
                _success.emit("Restore successfully!!!")
            }
        }
    }

    // Function to delete a recycle book
    fun deleteRecycleBook(id:String){
        viewModelScope.launch(Dispatchers.IO){
            // Call the repository to delete the recycle book
            recycleBookRepo.delete(id)
        }
    }

    // Function to delete a book from storage
    fun deleteStorageBook(url: String){
        viewModelScope.launch(Dispatchers.IO){
            // Call the StorageService to delete the book from storage
            storageService.deletePdf(url)
            // Emit success message
            _success.emit("Delete Successfully!!!")
        }
    }
}