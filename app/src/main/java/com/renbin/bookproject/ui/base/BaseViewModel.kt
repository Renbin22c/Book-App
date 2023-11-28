package com.renbin.bookproject.ui.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

abstract class BaseViewModel : ViewModel() {
    // SharedFlow for emitting error messages
    protected val _error: MutableSharedFlow<String> = MutableSharedFlow()
    val error: SharedFlow<String> = _error

    // SharedFlow for emitting success messages
    protected val _success: MutableSharedFlow<String> = MutableSharedFlow()
    val success: SharedFlow<String> = _success


    // SharedFlow for emitting loading state
    protected val _loading: MutableSharedFlow<Boolean>  = MutableSharedFlow()
    val loading: SharedFlow<Boolean> = _loading

    // Called when the ViewModel is created
    open fun onCreate(){}

    // A function for safely making API calls with error handling
    suspend fun <T> safeApiCall(callback: suspend () -> T?): T? {
        return try {
            callback()
        } catch (e: Exception) {
            e.printStackTrace()
            _error.emit(e.message ?: "Something went wrong")
            null
        }
    }
}