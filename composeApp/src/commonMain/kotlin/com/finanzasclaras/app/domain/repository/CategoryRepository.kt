package com.finanzasclaras.app.domain.repository

import com.finanzasclaras.app.domain.model.Category
import com.finanzasclaras.app.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getAll(): Flow<List<Category>>
    fun getByType(type: TransactionType): Flow<List<Category>>
    suspend fun getById(id: String): Category?
    suspend fun create(category: Category)
    suspend fun update(category: Category)
    suspend fun delete(id: String)
    suspend fun seedDefaultCategories()
}
