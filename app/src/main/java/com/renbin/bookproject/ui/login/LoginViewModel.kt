package com.renbin.bookproject.ui.login

import androidx.lifecycle.viewModelScope
import com.renbin.bookproject.core.service.AuthService
import com.renbin.bookproject.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authService: AuthService
): BaseViewModel() {

    fun login(email: String, pass: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = safeApiCall { authService.login(email, pass) }

            if (user == null) {
                _error.emit("Email or Password is wrong")
            } else {
                _success.emit("Login successful")
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            safeApiCall {
                authService.resetPassword(email)
                _success.emit("Reset password email send successfully !!!")
            }
        }
    }
}