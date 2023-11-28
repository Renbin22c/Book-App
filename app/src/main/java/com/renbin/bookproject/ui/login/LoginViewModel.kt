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

    // Function to initiate the login process
    fun login(email: String, pass: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // Attempt to login with the AuthService
            val user = safeApiCall { authService.login(email, pass) }

            if (user == null) {
                // Emit an error message if login fails
                _error.emit("Email or Password is wrong")
            } else {
                // Emit a success message on successful login
                _success.emit("Login successfully !!!")
            }
        }
    }

    // Function to initiate the password reset process
    fun resetPassword(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // Attempt to reset the password with the AuthService
            safeApiCall {
                authService.resetPassword(email)
                // Emit a success message on successful password reset
                _success.emit("Reset password email send successfully !!!")
            }
        }
    }
}