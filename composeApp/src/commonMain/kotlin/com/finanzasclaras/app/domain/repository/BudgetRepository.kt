package com.finanzasclaras.app.domain.repository

import com.finanzasclaras.app.domain.model.CategoryBudget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun getBudgetsForMonth(month: Int, year: Int): Flow<List<CategoryBudget>>
    suspend fun getBudgetForCategory(categoryId: String, month: Int, year: Int): CategoryBudget?
    suspend fun saveBudget(budget: CategoryBudget)
    suspend fun saveBudgets(budgets: List<CategoryBudget>)
    suspend fun deleteBudget(id: String)
    suspend fun clearMonthBudgets(month: Int, year: Int)
}
