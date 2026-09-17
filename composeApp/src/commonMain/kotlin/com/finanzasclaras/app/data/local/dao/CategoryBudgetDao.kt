package com.finanzasclaras.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.finanzasclaras.app.data.local.entity.CategoryBudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryBudgetDao {
    @Query("SELECT * FROM category_budgets WHERE month = :month AND year = :year")
    fun getByMonth(month: Int, year: Int): Flow<List<CategoryBudgetEntity>>

    @Query("SELECT * FROM category_budgets WHERE categoryId = :categoryId AND month = :month AND year = :year")
    suspend fun getByCategoryAndMonth(categoryId: String, month: Int, year: Int): CategoryBudgetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: CategoryBudgetEntity)

    @Query("DELETE FROM category_budgets WHERE id = :id")
    suspend fun delete(id: String)
}
