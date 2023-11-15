package com.renbin.bookproject.data.model

data class User(
    val id: String? = null,
    val name: String,
    val email: String,
    val profilePicUrl: String = "",
    val favourites: String = "0",
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "name" to name,
            "email" to email,
            "profilePicUrl" to profilePicUrl,
            "favourites" to favourites,
            "timestamp" to timestamp
        )
    }

    companion object {
        fun fromHashMap(hash: Map<String, Any>): User {
            return User(
                id = hash["id"].toString(),
                name = hash["name"].toString(),
                email = hash["email"].toString(),
                profilePicUrl = hash["profilePicUrl"].toString(),
                favourites = hash["favourites"].toString(),
                timestamp = hash["timestamp"].toString().toLong()
            )
        }
    }
}