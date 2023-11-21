package com.renbin.bookproject.data.repo

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.renbin.bookproject.data.model.Category
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class CategoryRepoRealtimeImpl(
    private val dbRef: DatabaseReference
) : CategoryRepo {
    override fun getAllCategories(uid: String) = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categories = mutableListOf<Category>()
                for (categorySnapshot in snapshot.children) {
                    val category = categorySnapshot.getValue(Category::class.java)
                    if(category != null && category.uid == uid){
                        categories.add(category.copy(id = categorySnapshot.key ?: ""))
                    }
                }
                trySend(categories)
            }

            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }

        }
        dbRef.addValueEventListener(listener)
        awaitClose()
    }

    override suspend fun insert(category: Category, uid: String) {
        val categoryWithUid = category.copy(uid = uid)
        dbRef.push().setValue(categoryWithUid.toHashMap()).await()
    }

    override suspend fun delete(id: String) {
        dbRef.child(id).removeValue()
    }

    override suspend fun getAllCategoryNames(uid: String): Flow<List<String>> {
        return flow {
            val categoryNames = dbRef.orderByChild("uid").equalTo(uid).get().await().children.mapNotNull {
                it.child("category").getValue(String::class.java)
            }
            emit(categoryNames)
        }
    }
}
