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

    private val _recycleBooks: MutableStateFlow<List<RecycleBook>> = MutableStateFlow(emptyList())
    val recycleBooks: StateFlow<List<RecycleBook>> = _recycleBooks

    private val user = authService.getCurrentUser()

    override fun onCreate() {
        super.onCreate()

        getAllRecycleBooks()
    }
    private fun getAllRecycleBooks(){
        viewModelScope.launch(Dispatchers.IO) {
            _loading.emit(true)
            user?.let { currentUser ->
                safeApiCall {
                    recycleBookRepo.getAllRecycleBooks(currentUser.uid)
                }?.collect {
                    _recycleBooks.value = it
                    _loading.emit(false)
                }
            }
        }
    }

    fun addBackBook(title: String, desc: String, category: String,
                    link: String, url: String, uid:String, timeStamp: Long){
        val book = Book(
            title = title, desc = desc, category = category, link = link,
            url = url, timestamp = timeStamp)
        viewModelScope.launch(Dispatchers.IO) {
            safeApiCall {
                bookRepo.insert(book, uid)
                _success.emit("Restore successfully!!!")
            }
        }
    }
    fun deleteRecycleBook(id:String){
        viewModelScope.launch(Dispatchers.IO){
            recycleBookRepo.delete(id)
        }
    }

    fun deleteStorageBook(url: String){
        viewModelScope.launch(Dispatchers.IO){
            storageService.deletePdf(url)
            _success.emit("Delete Successfully!!!")
        }
    }
}