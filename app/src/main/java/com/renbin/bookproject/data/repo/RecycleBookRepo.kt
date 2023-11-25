package com.renbin.bookproject.data.repo

import com.renbin.bookproject.data.model.Book
import com.renbin.bookproject.data.model.RecycleBook
import kotlinx.coroutines.flow.Flow

interface RecycleBookRepo {
    fun getAllRecycleBooks(uid: String): Flow<List<RecycleBook>>
    suspend fun insert(recycleBook: RecycleBook)
    suspend fun delete(id: String)
}