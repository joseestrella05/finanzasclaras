package com.finanzasclaras.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.finanzasclaras.app.data.local.entity.CategoryBudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryBudgetDao {
    @Query("SELECT * FROM category_budgets ORDER BY year DESC, month DESC")
    fun getAll(): Flow<List<CategoryBudgetEntity>>

    @Query("SELECT * FROM category_budgets WHERE month = :month AND year = :year")
    fun getByMonth(month: Int, year: Int): Flow<List<CategoryBudgetEntity>>

    @Query("SELECT * FROM category_budgets WHERE categoryId = :categoryId AND month = :month AND year = :year")
    suspend fun getByCategoryAndMonth(categoryId: String, month: Int, year: Int): CategoryBudgetEntity?

    @Query("SELECT * FROM category_budgets WHERE synced = 0")
    suspend fun getUnsynced(): List<CategoryBudgetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: CategoryBudgetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(budgets: List<CategoryBudgetEntity>)

    @Query("UPDATE category_budgets SET synced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)

    @Query("SELECT * FROM category_budgets")
    suspend fun getAllList(): List<CategoryBudgetEntity>

    @Query("DELETE FROM category_budgets WHERE month = :month AND year = :year")
    suspend fun deleteMonth(month: Int, year: Int)

    @Query("DELETE FROM category_budgets WHERE id = :id")
    suspend fun delete(id: String)
}
