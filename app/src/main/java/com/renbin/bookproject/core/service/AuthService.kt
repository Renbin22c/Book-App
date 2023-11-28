package com.renbin.bookproject.core.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AuthService(
    private val auth: FirebaseAuth = Firebase.auth
) {
    // Registers a new user with the provided email and password
    suspend fun register(email: String, pass: String): FirebaseUser? {
        val task = auth.createUserWithEmailAndPassword(email, pass).await()
        return task.user
    }

    // Log in an existing user with the provided email and password
    suspend fun login(email: String, pass: String): FirebaseUser? {
        val task = auth.signInWithEmailAndPassword(email, pass).await()
        return task.user
    }

    // Sends a password reset email to the specified email address
    suspend fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    // Retrieves the currently authenticated user
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    // Logs out the currently authenticated user
    fun logout() {
        return auth.signOut()
    }
}
