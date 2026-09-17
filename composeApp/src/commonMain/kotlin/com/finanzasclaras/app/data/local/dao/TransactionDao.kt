package com.finanzasclaras.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.finanzasclaras.app.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE deleted = 0 ORDER BY date DESC")
    fun getAll(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE deleted = 0 AND type = :type ORDER BY date DESC")
    fun getByType(type: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE deleted = 0 AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getByDateRange(startDate: Long, endDate: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE deleted = 0 AND type = :type AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getByTypeAndDateRange(type: String, startDate: Long, endDate: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE deleted = 0 AND categoryId = :categoryId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getByCategoryAndDateRange(categoryId: String, startDate: Long, endDate: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: String): TransactionEntity?

    @Query("SELECT SUM(amountInBase) FROM transactions WHERE deleted = 0 AND type = 'income' AND date BETWEEN :startDate AND :endDate")
    suspend fun getTotalIncome(startDate: Long, endDate: Long): Double?

    @Query("SELECT SUM(amountInBase) FROM transactions WHERE deleted = 0 AND type = 'expense' AND date BETWEEN :startDate AND :endDate")
    suspend fun getTotalExpense(startDate: Long, endDate: Long): Double?

    @Query("SELECT SUM(amountInBase) FROM transactions WHERE deleted = 0 AND type = 'expense' AND categoryId = :categoryId AND date BETWEEN :startDate AND :endDate")
    suspend fun getTotalExpenseByCategory(categoryId: String, startDate: Long, endDate: Long): Double?

    @Query("SELECT categoryId, SUM(amountInBase) as total FROM transactions WHERE deleted = 0 AND type = 'expense' AND date BETWEEN :startDate AND :endDate GROUP BY categoryId")
    fun getExpenseByCategory(startDate: Long, endDate: Long): Flow<List<CategoryExpense>>

    @Query("SELECT CAST(strftime('%m', date / 1000, 'unixepoch') AS INTEGER) as month, SUM(CASE WHEN type = 'income' THEN amountInBase ELSE 0 END) as income, SUM(CASE WHEN type = 'expense' THEN amountInBase ELSE 0 END) as expense FROM transactions WHERE deleted = 0 AND date BETWEEN :startDate AND :endDate GROUP BY month ORDER BY month")
    fun getMonthlySummary(startDate: Long, endDate: Long): Flow<List<MonthlySummary>>

    @Query("SELECT * FROM transactions WHERE synced = 0 AND deleted = 0")
    suspend fun getUnsynced(): List<TransactionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<TransactionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity)

    @Query("UPDATE transactions SET synced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)

    @Query("UPDATE transactions SET deleted = 1, updatedAt = :now WHERE id = :id")
    suspend fun softDelete(id: String, now: Long)
}

data class CategoryExpense(
    val categoryId: String,
    val total: Double
)

data class MonthlySummary(
    val month: Int,
    val income: Double,
    val expense: Double
)
