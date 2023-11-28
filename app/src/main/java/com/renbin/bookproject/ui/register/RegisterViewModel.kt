package com.renbin.bookproject.ui.register

import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.renbin.bookproject.core.service.AuthService
import com.renbin.bookproject.data.model.User
import com.renbin.bookproject.data.repo.UserRepo
import com.renbin.bookproject.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authService: AuthService,
    private val userRepo: UserRepo
): BaseViewModel() {

    // Function to initiate the registration process
    fun register(email: String, pass: String, pass2: String){
        viewModelScope.launch(Dispatchers.IO) {
            // Validate input data and get an error message
            val error = validate(email, pass, pass2)
            val timestamp = System.currentTimeMillis()

            if(!error.isNullOrEmpty()){
                _error.emit(error.toString())
            } else{
                // Attempt to register the user with the AuthService
                val user = safeApiCall { authService.register(email, pass) }

                if(user == null){
                    // Emit an error message if user registration fails
                    _error.emit("Cannot create user")
                } else{
                    // Add user details to the local repository on successful registration
                    userRepo.addUser(
                        user.uid,
                        User(name = timestamp.toString() , email = user.email ?: "")
                    )

                    // Emit a success message
                    _success.emit("Register successfully !!!")
                }
            }
        }
    }

    // Function to validate email, password, and confirm password
    private fun validate(email: String, pass: String, pass2: String): String? {
        return if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            "Please provide a valid email address"
        } else if (pass.length < 6) {
            "Password length must be greater than 5"
        } else if (pass != pass2) {
            "Password and Confirm Password not match"
        } else {
            null
        }
    }
}