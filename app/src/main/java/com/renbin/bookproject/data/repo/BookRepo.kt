package com.renbin.bookproject.data.repo

import com.renbin.bookproject.data.model.Book
import kotlinx.coroutines.flow.Flow

interface BookRepo {
    // Gets all books associated with a user
    fun getAllBooks(uid: String): Flow<List<Book>>

    // Gets books of a specific category associated with a user
    fun getBookByCategory(category: String, uid: String): Flow<List<Book>>

    // Gets favorite books associated with a user
    fun getBookByFavourite(uid: String): Flow<List<Book>>

    // Inserts a new book associated with a user
    suspend fun insert(book: Book, uid: String)

    // Gets a specific book by its ID
    suspend fun getBook(id: String): Book?

    // Updates a book with a new set of data
    suspend fun update(id: String, book: Book)

    // Deletes a book by its ID
    suspend fun delete(id: String)
}