package com.renbin.bookproject.data.repo

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.renbin.bookproject.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserRepoImpl(
    private val dbRef: CollectionReference
): UserRepo {
    // Adds a new user to the repository
    override suspend fun addUser(id: String, user: User) {
        dbRef.document(id).set(user.toHashMap())
    }

    // Retrieves a user's data in real-time using a Flow
    override fun getUser(id: String): Flow<User> = callbackFlow {
        val snapshot = dbRef.document(id).get().await()
        snapshot.data?.let {
            // Set the "id" field in the user data
            it["id"] = snapshot.id
            // Try sending the user data through the flow and close the flow
            trySend(User.fromHashMap(it)).isSuccess
        }
        close()
    }

    // Updates a user's data in the repository
    override suspend fun updateUser(id: String, user: User) {
        dbRef.document(id).set(user.toHashMap()).await()
    }
}