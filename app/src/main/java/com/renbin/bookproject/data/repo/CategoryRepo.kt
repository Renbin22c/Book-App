package com.renbin.bookproject.data.repo

import com.renbin.bookproject.data.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepo {
    // Retrieves all categories associated with a user in real-time using a Flow
    fun getAllCategories(uid: String): Flow<List<Category>>

    // Inserts a new category associated with a user into the database
    suspend fun insert(category: Category, uid: String)

    // Deletes a specific category from the database by its ID
    suspend fun delete(id: String)

    // Retrieves all category names associated with a user in real-time using a Flow
    suspend fun getAllCategoryNames(uid: String): Flow<List<String>>
}