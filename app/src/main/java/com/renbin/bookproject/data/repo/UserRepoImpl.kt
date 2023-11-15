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
    override suspend fun addUser(id: String, user: User) {
        dbRef.document(id).set(user.toHashMap())
    }

    override fun getUser(id: String): Flow<User> = callbackFlow {
        val snapshot = dbRef.document(id).get().await()
        snapshot.data?.let {
            it["id"] = snapshot.id
            trySend(User.fromHashMap(it)).isSuccess
        }
        close()
    }




    override suspend fun updateUser(id: String, user: User) {
        dbRef.document(id).set(user.toHashMap()).await()
    }
}