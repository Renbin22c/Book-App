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
    private val _user = MutableStateFlow(User(name = "Unknown", email = "Unknown"))
    val user: StateFlow<User> = _user

    private val _profileUri = MutableStateFlow<Uri?>(null)
    val profileUri: StateFlow<Uri?> = _profileUri

    private val _books: MutableStateFlow<List<Book>> = MutableStateFlow(emptyList())
    val books: StateFlow<List<Book>> = _books

    private val firebaseUser = authService.getCurrentUser()

    override fun onCreate() {
        super.onCreate()
        getCurrentUser()
        getProfilePicUri()
    }

    fun getCurrentUser() {
        firebaseUser?.let {
            viewModelScope.launch(Dispatchers.IO) {
                safeApiCall { userRepo.getUser(it.uid) }?.collect { user ->
                    _user.value = user
                    getBookByFavourite()
                }
            }
        }
    }

    fun updateProfilePic(uri: Uri){
        user.value.id?.let {
            viewModelScope.launch(Dispatchers.IO) {
                val name = "Users/$it.jpg"
                storageService.addImage(name, uri)
                getProfilePicUri()
            }
        }
    }

    private fun getProfilePicUri(){
        firebaseUser?.uid.let {
            viewModelScope.launch(Dispatchers.IO) {
                _profileUri.value = storageService.getImage("Users/$it.jpg")
            }
        }
    }

    fun updateName(name: String){
        firebaseUser?.let {
            viewModelScope.launch(Dispatchers.IO) {
                val error = validate(name)
                if(!error.isNullOrEmpty()){
                    _error.emit(error.toString())
                } else {
                    safeApiCall { userRepo.updateUser(it.uid, user.value.copy(name = name)) }
                    _success.emit("Update Successfully!!!")
                }
            }
        }
    }

    private fun updateUserFavourite(favouriteCount: String){
        firebaseUser?.let {
            viewModelScope.launch(Dispatchers.IO) {
                safeApiCall {
                    userRepo.updateUser(it.uid, user.value.copy(favourites = favouriteCount))
                }
            }
        }
    }

    fun updateFavourite(favourite: Boolean, id: String){
        viewModelScope.launch(Dispatchers.IO) {
            val book = bookRepo.getBook(id)
            safeApiCall {
                book?.let {
                    bookRepo.update(id, it.copy(favourite = favourite))
                    if (favourite){
                        _success.emit("Add to Favourite Successfully !!!")
                    } else{
                        _success.emit("Remove from Favourite Successfully !!!")
                    }
                }
            }
        }
    }

    private fun validate(name: String): String?{
        return if(name.isEmpty()){
            "Name cannot be empty"
        } else {
            null
        }
    }

    private fun getBookByFavourite(){
        firebaseUser?.let {
            viewModelScope.launch(Dispatchers.IO){
                _loading.emit(true)
                safeApiCall {
                    bookRepo.getBookByFavourite(it.uid)
                }?.collect{
                   _books.value = it
                    val count = it.count().toString()
                    updateUserFavourite(count)
                    getCurrentUser()
                    _loading.emit(false)
                }
            }
        }
    }

}