package com.finanzasclaras.app.domain.repository

import com.finanzasclaras.app.data.local.dao.CategoryExpense
import com.finanzasclaras.app.data.local.dao.MonthlySummary
import com.finanzasclaras.app.domain.model.Transaction
import com.finanzasclaras.app.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAll(): Flow<List<Transaction>>
    fun getByType(type: TransactionType): Flow<List<Transaction>>
    fun getByDateRange(startDate: Long, endDate: Long): Flow<List<Transaction>>
    fun getByTypeAndDateRange(type: TransactionType, startDate: Long, endDate: Long): Flow<List<Transaction>>
    fun getByCategoryAndDateRange(categoryId: String, startDate: Long, endDate: Long): Flow<List<Transaction>>
    suspend fun getById(id: String): Transaction?
    suspend fun create(transaction: Transaction)
    suspend fun update(transaction: Transaction)
    suspend fun softDelete(id: String)
    suspend fun getTotalIncome(startDate: Long, endDate: Long): Double
    suspend fun getTotalExpense(startDate: Long, endDate: Long): Double
    fun getMonthlySummary(startDate: Long, endDate: Long): Flow<List<MonthlySummary>>
    fun getExpenseByCategory(startDate: Long, endDate: Long): Flow<List<CategoryExpense>>
}
