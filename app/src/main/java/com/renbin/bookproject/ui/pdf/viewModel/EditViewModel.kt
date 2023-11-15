package com.renbin.bookproject.ui.pdf.viewModel

import androidx.lifecycle.viewModelScope
import com.renbin.bookproject.core.service.AuthService
import com.renbin.bookproject.data.model.Book
import com.renbin.bookproject.data.repo.BookRepo
import com.renbin.bookproject.data.repo.CategoryRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPdfViewModel@Inject constructor(
    categoryRepo: CategoryRepo,
    authService: AuthService,
    private val bookRepo: BookRepo
): BaseAddEditPdfViewModel(categoryRepo, authService) {
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

    fun submit(title: String, desc: String, category: String) {
//        val error = validate(title, desc, category)
//        if(!error.isNullOrEmpty()){
//            viewModelScope.launch {
//                _error.emit(error.toString())
//            }
//        } else {
        viewModelScope.launch(Dispatchers.IO) {
            safeApiCall {
                bookRepo.update(
                    book.value.id,
                    book.value.copy(title = title, desc = desc, category = category))
            }
            _success.emit("Update Book Successfully !")
        }
     }
//    }
}