package com.renbin.bookproject.data.repo

import com.renbin.bookproject.data.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepo {
    suspend fun addUser(id: String, user: User)
    fun getUser(id: String): Flow<User>
    suspend fun updateUser(id: String, user: User)
}