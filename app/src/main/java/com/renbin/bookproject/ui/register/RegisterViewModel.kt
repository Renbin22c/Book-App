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

    fun register(email: String, pass: String, pass2: String){
        viewModelScope.launch(Dispatchers.IO) {
            val error = validate(email, pass, pass2)
            val timestamp = System.currentTimeMillis()
            if(!error.isNullOrEmpty()){
                _error.emit(error.toString())
            } else{
                val user = safeApiCall { authService.register(email, pass) }
                if(user == null){
                    _error.emit("Cannot create user")
                } else{
                    userRepo.addUser(
                        user.uid,
                        User(name = timestamp.toString() , email = user.email ?: "")
                    )
                    _success.emit("Register successful")
                }
            }
        }
    }

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