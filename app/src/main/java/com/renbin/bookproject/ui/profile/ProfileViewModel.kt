package com.renbin.bookproject.ui.profile

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.renbin.bookproject.core.service.AuthService
import com.renbin.bookproject.core.service.StorageService
import com.renbin.bookproject.data.model.Book
import com.renbin.bookproject.data.model.User
import com.renbin.bookproject.data.repo.BookRepo
import com.renbin.bookproject.data.repo.UserRepo
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
class ProfileViewModel @Inject constructor(
    authService: AuthService,
    private val storageService: StorageService,
    private val userRepo: UserRepo,
    private val bookRepo: BookRepo
): BaseViewModel(){
    // StateFlow to hold the user information
    private val _user = MutableStateFlow(User(name = "Unknown", email = "Unknown"))
    val user: StateFlow<User> = _user

    // StateFlow to hold the profile picture URI
    private val _profileUri = MutableStateFlow<Uri?>(null)
    val profileUri: StateFlow<Uri?> = _profileUri

    // StateFlow to hold the list of books associated with the user
    private val _books: MutableStateFlow<List<Book>> = MutableStateFlow(emptyList())
    val books: StateFlow<List<Book>> = _books

    // MutableStateFlow to hold the count of favorite books
    val favourites = MutableStateFlow(0)

    // Get the current Firebase user
    private val firebaseUser = authService.getCurrentUser()


    override fun onCreate() {
        super.onCreate()
        // Initialize user information and associated data
        getCurrentUser()
        getProfilePicUri()
    }

    // Function to get the current user's information
    fun getCurrentUser() {
        firebaseUser?.let {
            viewModelScope.launch(Dispatchers.IO) {
                // Retrieve user information from the repository
                safeApiCall { userRepo.getUser(it.uid) }?.collect { user ->
                    _user.value = user
                    // Get the books associated with the user and update favorites count
                    getBookByFavourite()
                }
            }
        }
    }

    // Function to update the user's profile picture
    fun updateProfilePic(uri: Uri){
        user.value.id?.let {
            viewModelScope.launch(Dispatchers.IO) {
                val name = "Users/$it.jpg"
                // Upload the image to the storage service
                storageService.addImage(name, uri)
                // Refresh the profile picture URI
                getProfilePicUri()
            }
        }
    }

    // Function to get the profile picture URI
    private fun getProfilePicUri(){
        firebaseUser?.uid.let {
            viewModelScope.launch(Dispatchers.IO) {
                _profileUri.value = storageService.getImage("Users/$it.jpg")
            }
        }
    }

    // Function to update the user's name
    fun updateName(name: String){
        firebaseUser?.let {
            viewModelScope.launch(Dispatchers.IO) {
                // Validate the new name
                val error = validate(name)
                if(!error.isNullOrEmpty()){
                    _error.emit(error.toString())
                } else {
                    // Update the user's name in the repository
                    safeApiCall { userRepo.updateUser(it.uid, user.value.copy(name = name)) }
                    _success.emit("Update Successfully!!!")
                }
            }
        }
    }

    // Function to update the favorite status of a book
    fun updateFavourite(favourite: Boolean, id: String){
        viewModelScope.launch(Dispatchers.IO) {
            // Retrieve the book based on its ID
            val book = bookRepo.getBook(id)
            safeApiCall {
                book?.let {
                    // Update the favorite status in the repository
                    bookRepo.update(id, it.copy(favourite = favourite))
                    // Emit success message based on the action
                    if (favourite){
                        _success.emit("Add to Favourite Successfully !!!")
                    } else{
                        _success.emit("Remove from Favourite Successfully !!!")
                    }
                }
            }
        }
    }

    // Function to validate the user's name
    private fun validate(name: String): String?{
        return if(name.isEmpty()){
            "Name cannot be empty"
        } else {
            null
        }
    }

    // Function to get books associated with the user based on favorite status
    private fun getBookByFavourite(){
        firebaseUser?.let {
            viewModelScope.launch(Dispatchers.IO){
                _loading.emit(true)
                safeApiCall {
                    // Retrieve books based on favorite status
                    bookRepo.getBookByFavourite(it.uid)
                }?.collect{
                   _books.value = it
                    // Update the count of favorite books
                    favourites.emit(it.size)
                    _loading.emit(false)
                }
            }
        }
    }

}