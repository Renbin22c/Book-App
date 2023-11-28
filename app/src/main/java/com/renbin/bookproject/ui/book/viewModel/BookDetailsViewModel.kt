package com.renbin.bookproject.ui.book.viewModel

import androidx.lifecycle.viewModelScope
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
class BookDetailsViewModel @Inject constructor(
    private val bookRepo: BookRepo
): BaseViewModel() {

    // MutableStateFlow representing the current book state
    private val _book: MutableStateFlow<Book> = MutableStateFlow(
        Book(title = "", desc = "", category = "")
    )
    // StateFlow exposing the current book state to observers
    val book: StateFlow<Book> = _book

    // Retrieves a specific book by its ID
    fun getBook(id:String){
        viewModelScope.launch(Dispatchers.IO) {
            safeApiCall {
                bookRepo.getBook(id)
            }?.let {
                _book.value =it
            }
        }
    }

    // Updates the favorite status of the current book
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