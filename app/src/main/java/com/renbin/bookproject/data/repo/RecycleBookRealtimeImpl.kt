package com.renbin.bookproject.data.repo

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.renbin.bookproject.data.model.Book
import com.renbin.bookproject.data.model.RecycleBook
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class RecycleBookRealtimeImpl(
    private val dbRef: DatabaseReference
): RecycleBookRepo {
    // Retrieves all recycled books associated with a user in real-time using a Flow
    override fun getAllRecycleBooks(uid: String) = callbackFlow{
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val recycleBooks = mutableListOf<RecycleBook>()
                for(recycleBookSnapshot in snapshot.children){
                    val recycleBook = recycleBookSnapshot.getValue(RecycleBook::class.java)
                    if (recycleBook != null && recycleBook.uid == uid) {
                        recycleBooks.add(recycleBook.copy(id = recycleBookSnapshot.key ?: ""))
                    }
                }
                trySend(recycleBooks)
            }

            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }

        }
        dbRef.addValueEventListener(listener)
        awaitClose()
    }

    // Inserts a new recycled book into the database
    override suspend fun insert(recycleBook: RecycleBook) {
        dbRef.push().setValue(recycleBook.toHashMap()).await()
    }

    // Deletes a specific recycled book from the database by its ID
    override suspend fun delete(id: String) {
        dbRef.child(id).removeValue()
    }
}