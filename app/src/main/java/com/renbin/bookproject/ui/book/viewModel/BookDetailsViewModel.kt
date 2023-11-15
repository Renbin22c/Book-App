package com.renbin.bookproject.ui.book.viewModel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.renbin.bookproject.core.service.AuthService
import com.renbin.bookproject.data.model.Book
import com.renbin.bookproject.data.model.User
import com.renbin.bookproject.data.repo.BookRepo
import com.renbin.bookproject.data.repo.UserRepo
import com.renbin.bookproject.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    private val bookRepo: BookRepo
): BaseViewModel() {
    private val _book: MutableStateFlow<Book> = MutableStateFlow(
        Book(title = "", desc = "", category = "")
    )
    val book: StateFlow<Book> = _book

    fun getBook(id:String){
        viewModelScope.launch(Dispatchers.IO) {
            safeApiCall {
                bookRepo.getBook(id)
            }?.let {
                _book.value =it
            }
        }
    }

    fun updateBookFavourite(favourite: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            safeApiCall {
                bookRepo.update(
                    book.value.id,
                    book.value.copy(favourite = favourite))
                if (favourite){
                    _success.emit("Add to Favourite Successfully !!!")
                } else{
                    _success.emit("Remove from Favourite Successfully !!!")
                }
            }
        }
    }

}