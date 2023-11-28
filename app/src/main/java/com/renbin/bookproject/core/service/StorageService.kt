package com.renbin.bookproject.core.service

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

class StorageService(
    private val storage: StorageReference = FirebaseStorage.getInstance().reference
) {
    // Adds an image to the storage with the specified name
    suspend fun addImage(name: String, uri: Uri){
        storage.child(name).putFile(uri).await()
    }

    // Retrieves the download URL for the image with the specified name
    suspend fun getImage(name: String): Uri?{
        return try {
            storage.child(name).downloadUrl.await()
        }catch (e:Exception){
            e.printStackTrace()
            null
        }
    }

    // Deletes the PDF file with the specified URL from the storage
    suspend fun deletePdf(url:String){
        storage.child(url).delete().await()
    }
}