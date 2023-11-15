package com.renbin.bookproject.data.repo

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.renbin.bookproject.data.model.Book
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class BookRepoRealtimeImpl(
    private val dbRef: DatabaseReference
): BookRepo {
    override fun getAllBooks(uid: String) = callbackFlow{
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val books = mutableListOf<Book>()
                for(bookSnapshot in snapshot.children){
                    val book = bookSnapshot.getValue(Book::class.java)
                    if (book != null && book.uid == uid) {
                        books.add(book.copy(id = bookSnapshot.key ?: ""))
                    }
                }
                trySend(books)
            }

            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }

        }
        dbRef.addValueEventListener(listener)
        awaitClose()
    }

    override fun getBookByCategory(category: String, uid: String): Flow<List<Book>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val books = mutableListOf<Book>()
                for (bookSnapshot in snapshot.children) {
                    val book = bookSnapshot.getValue(Book::class.java)
                    if (book != null && book.uid == uid && book.category == category) {
                        books.add(book.copy(id = bookSnapshot.key ?: ""))
                    }
                }
                trySend(books)
            }

            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }
        }

        dbRef.addValueEventListener(listener)
        awaitClose()
    }

    override fun getBookByFavourite(uid: String): Flow<List<Book>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val favoriteBooks = mutableListOf<Book>()
                for (bookSnapshot in snapshot.children) {
                    val book = bookSnapshot.getValue(Book::class.java)
                    if (book != null && book.uid == uid && book.favourite) {
                        favoriteBooks.add(book.copy(id = bookSnapshot.key ?: ""))
                    }
                }
                trySend(favoriteBooks)
            }

            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }
        }

        dbRef.addValueEventListener(listener)
        awaitClose()
    }

    override suspend fun insert(book: Book, uid: String) {
        val bookWithUid = book.copy(uid = uid)
        dbRef.push().setValue(bookWithUid.toHashMap()).await()
    }

    override suspend fun getBook(id: String): Book? {
        val item = dbRef.child(id).get().await()
        return item.key?.let {
            item.getValue<Book>()?.copy(id = it)
        }
    }

    override suspend fun update(id: String, book: Book) {
        if(id.isEmpty()) return
        dbRef.child(id).setValue(book.toHashMap())
    }

    override suspend fun delete(id: String) {
        dbRef.child(id).removeValue()
    }
}