package com.renbin.bookproject.data.model

import androidx.room.Entity

@Entity
data class Book(
    val id: String = "",
    val title: String = "",
    val desc: String = "",
    val category: String = "",
    val favourite: Boolean = false,
    val url: String = "",
    val link: String ="",
    val uid: String = "",
    val timestamp: Long = System.currentTimeMillis()
){
    fun toHashMap(): HashMap<String, Any>{
        return hashMapOf(
            "title" to title,
            "desc" to desc,
            "category" to category,
            "favourite" to favourite,
            "url" to url,
            "link" to link,
            "uid" to uid,
            "timestamp" to timestamp
        )
    }

    companion object{
        fun fromHashMap(hash: Map<String, Any>): Book{
            return Book(
                id = hash["id"].toString(),
                title = hash["title"].toString(),
                desc = hash["desc"].toString(),
                category = hash["category"].toString(),
                favourite = hash["favourite"] as Boolean,
                url = hash["url"].toString(),
                link = hash["link"].toString(),
                uid = hash["uid"].toString(),
                timestamp = hash["timestamp"].toString().toLong()
            )
        }
    }
}