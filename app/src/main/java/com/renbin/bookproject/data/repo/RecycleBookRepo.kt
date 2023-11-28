package com.renbin.bookproject.data.repo

import com.renbin.bookproject.data.model.Book
import com.renbin.bookproject.data.model.RecycleBook
import kotlinx.coroutines.flow.Flow

interface RecycleBookRepo {
    // Retrieves all recycled books associated with a user in real-time using a Flow
    fun getAllRecycleBooks(uid: String): Flow<List<RecycleBook>>

    // Inserts a new recycled book into the database
    suspend fun insert(recycleBook: RecycleBook)

    // Deletes a specific recycled book from the database by its ID
    suspend fun delete(id: String)
}