package com.finanzasclaras.app.presentation.budgets

import com.finanzasclaras.app.domain.logic.FinancialPlanRecommendation
import com.finanzasclaras.app.domain.model.Category
import com.finanzasclaras.app.domain.model.CategoryBudget

data class CategoryBudgetItem(
    val budgetId: String,
    val categoryId: String,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: Int,
    val monthlyLimit: Double,
    val spent: Double,
    val remaining: Double,
    val percentage: Double,
    val isExceeded: Boolean,
    val overspentAmount: Double
)

data class BudgetsUiState(
    val selectedMonth: Int = 1,
    val selectedYear: Int = 2026,
    val baseCurrency: String = "DOP",
    val monthlyIncome: Double = 0.0,
    val totalBudget: Double = 0.0,
    val totalSpent: Double = 0.0,
    val totalRemaining: Double = 0.0,
    val overallPercentage: Double = 0.0,
    val isOverallExceeded: Boolean = false,
    val items: List<CategoryBudgetItem> = emptyList(),
    val availableCategories: List<Category> = emptyList(),
    val allCategories: List<Category> = emptyList(),
    val planRecommendation: FinancialPlanRecommendation? = null,
    val isLoading: Boolean = true,
    val showAddEditDialog: Boolean = false,
    val editingBudgetId: String? = null,
    val dialogCategoryId: String = "",
    val dialogLimitText: String = "",
    val showIncomeDialog: Boolean = false,
    val incomeInputText: String = "",
    val message: String? = null
)
