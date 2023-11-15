package com.renbin.bookproject.data.model

import androidx.room.Entity

@Entity
data class Category(
    val id: String = "",
    val category: String = "",
    val uid: String = "",
    val timestamp: Long = System.currentTimeMillis()
){
    fun toHashMap(): HashMap<String, Any>{
        return hashMapOf(
            "category" to category,
            "uid" to uid,
            "timestamp" to timestamp
        )
    }

    companion object{
        fun fromHashMap(hash: Map<String, Any>): Category{
            return Category(
                id = hash["id"].toString(),
                category = hash["category"].toString(),
                uid = hash["uid"].toString(),
                timestamp = hash["timestamp"].toString().toLong()
            )
        }
    }
}