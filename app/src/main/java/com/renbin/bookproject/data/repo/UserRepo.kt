package com.renbin.bookproject.data.repo

import com.renbin.bookproject.data.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepo {
    // Adds a new user to the repository
    suspend fun addUser(id: String, user: User)

    // Retrieves a user's data in real-time using a Flow
    fun getUser(id: String): Flow<User>

    // Updates a user's data in the repository
    suspend fun updateUser(id: String, user: User)
}