package com.renbin.bookproject.data.repo

import com.renbin.bookproject.data.model.Book
import kotlinx.coroutines.flow.Flow

interface BookRepo {
    fun getAllBooks(uid: String): Flow<List<Book>>
    fun getBookByCategory(category: String, uid: String): Flow<List<Book>>
    fun getBookByFavourite(uid: String): Flow<List<Book>>
    suspend fun insert(book: Book, uid: String)
    suspend fun getBook(id: String): Book?
    suspend fun update(id: String, book: Book)
    suspend fun delete(id: String)
}