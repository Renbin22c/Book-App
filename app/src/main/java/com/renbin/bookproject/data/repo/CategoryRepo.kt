package com.renbin.bookproject.data.repo

import com.renbin.bookproject.data.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepo {
    fun getAllCategories(uid: String): Flow<List<Category>>
    suspend fun insert(category: Category, uid: String)
    suspend fun delete(id: String)
    suspend fun getAllCategoryNames(uid: String): Flow<List<String>>
}