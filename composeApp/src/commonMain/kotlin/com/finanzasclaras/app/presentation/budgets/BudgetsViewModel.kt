package com.finanzasclaras.app.presentation.budgets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzasclaras.app.core.local.UserPreferences
import com.finanzasclaras.app.core.util.CurrencyUtils
import com.finanzasclaras.app.core.util.DateUtils
import com.finanzasclaras.app.core.util.IdUtils
import com.finanzasclaras.app.data.remote.firebase.FirebaseSyncManager
import com.finanzasclaras.app.domain.logic.BudgetRecommendationEngine
import com.finanzasclaras.app.domain.model.Category
import com.finanzasclaras.app.domain.model.CategoryBudget
import com.finanzasclaras.app.domain.model.TransactionType
import com.finanzasclaras.app.domain.repository.BudgetRepository
import com.finanzasclaras.app.domain.repository.CategoryRepository
import com.finanzasclaras.app.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class BudgetsViewModel(
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository,
    private val userPreferences: UserPreferences,
    private val syncManager: FirebaseSyncManager
) : ViewModel() {

    private val today = DateUtils.today()
    private val _state = MutableStateFlow(
        BudgetsUiState(
            selectedMonth = today.monthNumber,
            selectedYear = today.year
        )
    )
    val state: StateFlow<BudgetsUiState> = _state

    init {
        loadData()
    }

    fun onMonthChanged(delta: Int) {
        var newMonth = _state.value.selectedMonth + delta
        var newYear = _state.value.selectedYear
        if (newMonth < 1) {
            newMonth = 12
            newYear -= 1
        } else if (newMonth > 12) {
            newMonth = 1
            newYear += 1
        }
        _state.value = _state.value.copy(
            selectedMonth = newMonth,
            selectedYear = newYear
        )
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            val month = _state.value.selectedMonth
            val year = _state.value.selectedYear

            val startDate = LocalDate(year, month, 1)
            val endDate = if (month == 12) LocalDate(year, 12, 31) else LocalDate(year, month + 1, 1).let {
                DateUtils.endOfDay(LocalDate(year, month, 28)) // rough fallback or exact
            }

            val startMillis = DateUtils.toEpochMillis(startDate)
            val endMillis = DateUtils.toEpochMillisEnd(
                LocalDate(year, month, when (month) {
                    2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
                    4, 6, 9, 11 -> 30
                    else -> 31
                })
            )

            combine(
                budgetRepository.getBudgetsForMonth(month, year),
                categoryRepository.getAll(),
                transactionRepository.getByTypeAndDateRange(TransactionType.EXPENSE, startMillis, endMillis),
                userPreferences.preferences
            ) { budgets, categories, expenses, prefs ->
                val currency = prefs.baseCurrency
                val expenseCategories = categories.filter { it.type == TransactionType.EXPENSE }
                val categoriesMap = expenseCategories.associateBy { it.id }

                // Calculate actual spending per category
                val spentMap = mutableMapOf<String, Double>()
                for (tx in expenses) {
                    val current = spentMap[tx.categoryId] ?: 0.0
                    spentMap[tx.categoryId] = current + tx.amountInBase
                }

                var totalBudget = 0.0
                var totalSpentOnBudgets = 0.0

                val items = budgets.mapNotNull { b ->
                    val cat = categoriesMap[b.categoryId]
                    val catName = cat?.name ?: "Categoría"
                    val catIcon = cat?.icon ?: "tag"
                    val catColor = cat?.color ?: 0xFF4CAF50.toInt()
                    val spent = spentMap[b.categoryId] ?: 0.0
                    val remaining = b.monthlyLimit - spent
                    val pct = if (b.monthlyLimit > 0) (spent / b.monthlyLimit) * 100.0 else 0.0
                    val isExceeded = spent > b.monthlyLimit
                    val overspent = if (isExceeded) spent - b.monthlyLimit else 0.0

                    totalBudget += b.monthlyLimit
                    totalSpentOnBudgets += spent

                    CategoryBudgetItem(
                        budgetId = b.id,
                        categoryId = b.categoryId,
                        categoryName = catName,
                        categoryIcon = catIcon,
                        categoryColor = catColor,
                        monthlyLimit = CurrencyUtils.round(b.monthlyLimit),
                        spent = CurrencyUtils.round(spent),
                        remaining = CurrencyUtils.round(remaining),
                        percentage = CurrencyUtils.round(pct),
                        isExceeded = isExceeded,
                        overspentAmount = CurrencyUtils.round(overspent)
                    )
                }.sortedByDescending { it.percentage }

                val budgetedCategoryIds = budgets.map { it.categoryId }.toSet()
                val availableCategories = expenseCategories.filter { it.id !in budgetedCategoryIds }

                // Income determination: prefs.monthlyIncome or auto-detected income
                var monthlyIncome = prefs.monthlyIncome
                if (monthlyIncome <= 0.0) {
                    val actualIncome = transactionRepository.getTotalIncome(startMillis, endMillis)
                    if (actualIncome > 0.0) {
                        monthlyIncome = actualIncome
                    }
                }

                val recommendation = BudgetRecommendationEngine.generatePlan(monthlyIncome)

                val overallPct = if (totalBudget > 0) (totalSpentOnBudgets / totalBudget) * 100.0 else 0.0

                _state.value.copy(
                    baseCurrency = currency,
                    monthlyIncome = monthlyIncome,
                    totalBudget = CurrencyUtils.round(totalBudget),
                    totalSpent = CurrencyUtils.round(totalSpentOnBudgets),
                    totalRemaining = CurrencyUtils.round(totalBudget - totalSpentOnBudgets),
                    overallPercentage = CurrencyUtils.round(overallPct),
                    isOverallExceeded = totalSpentOnBudgets > totalBudget && totalBudget > 0,
                    items = items,
                    availableCategories = availableCategories,
                    allCategories = expenseCategories,
                    planRecommendation = recommendation,
                    isLoading = false
                )
            }.collect { newState ->
                _state.value = newState
            }
        }
    }

    fun openAddDialog() {
        val firstAvailable = _state.value.availableCategories.firstOrNull()?.id ?: ""
        _state.value = _state.value.copy(
            showAddEditDialog = true,
            editingBudgetId = null,
            dialogCategoryId = firstAvailable,
            dialogLimitText = ""
        )
    }

    fun openEditDialog(item: CategoryBudgetItem) {
        _state.value = _state.value.copy(
            showAddEditDialog = true,
            editingBudgetId = item.budgetId,
            dialogCategoryId = item.categoryId,
            dialogLimitText = if (item.monthlyLimit > 0) item.monthlyLimit.toLong().toString() else ""
        )
    }

    fun closeDialog() {
        _state.value = _state.value.copy(showAddEditDialog = false)
    }

    fun onDialogCategorySelected(categoryId: String) {
        _state.value = _state.value.copy(dialogCategoryId = categoryId)
    }

    fun onDialogLimitChanged(text: String) {
        _state.value = _state.value.copy(dialogLimitText = text)
    }

    fun saveBudget() {
        val catId = _state.value.dialogCategoryId
        val limit = _state.value.dialogLimitText.toDoubleOrNull() ?: return
        if (catId.isBlank() || limit <= 0) return

        val existingId = _state.value.editingBudgetId ?: IdUtils.randomId()
        val budget = CategoryBudget(
            id = existingId,
            categoryId = catId,
            monthlyLimit = limit,
            month = _state.value.selectedMonth,
            year = _state.value.selectedYear
        )

        viewModelScope.launch {
            budgetRepository.saveBudget(budget)
            closeDialog()
        }
    }

    fun deleteBudget(id: String) {
        viewModelScope.launch {
            budgetRepository.deleteBudget(id)
        }
    }

    fun openIncomeDialog() {
        _state.value = _state.value.copy(
            showIncomeDialog = true,
            incomeInputText = if (_state.value.monthlyIncome > 0) _state.value.monthlyIncome.toLong().toString() else ""
        )
    }

    fun closeIncomeDialog() {
        _state.value = _state.value.copy(showIncomeDialog = false)
    }

    fun onIncomeInputChanged(text: String) {
        _state.value = _state.value.copy(incomeInputText = text)
    }

    fun saveMonthlyIncome() {
        val income = _state.value.incomeInputText.toDoubleOrNull() ?: return
        if (income <= 0) return

        viewModelScope.launch {
            userPreferences.setMonthlyIncome(income)
            closeIncomeDialog()
        }
    }

    fun applyRecommendedPlan() {
        val income = _state.value.monthlyIncome
        if (income <= 0) {
            openIncomeDialog()
            return
        }

        viewModelScope.launch {
            val categories = _state.value.allCategories
            val month = _state.value.selectedMonth
            val year = _state.value.selectedYear

            val newBudgets = mutableListOf<CategoryBudget>()
            for (cat in categories) {
                val limit = BudgetRecommendationEngine.getSuggestedLimitForCategory(cat.name, income)
                if (limit > 0) {
                    val existing = budgetRepository.getBudgetForCategory(cat.id, month, year)
                    newBudgets.add(
                        CategoryBudget(
                            id = existing?.id ?: IdUtils.randomId(),
                            categoryId = cat.id,
                            monthlyLimit = CurrencyUtils.round(limit),
                            month = month,
                            year = year
                        )
                    )
                }
            }

            if (newBudgets.isNotEmpty()) {
                budgetRepository.saveBudgets(newBudgets)
            }
        }
    }
}
