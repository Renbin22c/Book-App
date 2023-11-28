package com.renbin.bookproject.data.model

data class User(
    val id: String? = null,
    val name: String,
    val email: String,
    val profilePicUrl: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    // Converts the User object to a HashMap for storing in a database
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "name" to name,
            "email" to email,
            "profilePicUrl" to profilePicUrl,
            "timestamp" to timestamp
        )
    }

    // Creates a User object from a HashMap retrieved from the database
    companion object {
        fun fromHashMap(hash: Map<String, Any>): User {
            return User(
                id = hash["id"].toString(),
                name = hash["name"].toString(),
                email = hash["email"].toString(),
                profilePicUrl = hash["profilePicUrl"].toString(),
                timestamp = hash["timestamp"].toString().toLong()
            )
        }
    }
}