package com.finanzasclaras.app.presentation.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzasclaras.app.core.local.UserPreferences
import com.finanzasclaras.app.core.util.CurrencyUtils
import com.finanzasclaras.app.core.util.DateUtils
import com.finanzasclaras.app.data.local.dao.CategoryExpense
import com.finanzasclaras.app.data.local.dao.MonthlySummary
import com.finanzasclaras.app.domain.model.Category
import com.finanzasclaras.app.domain.repository.CategoryRepository
import com.finanzasclaras.app.domain.repository.InvestmentRepository
import com.finanzasclaras.app.domain.repository.SavingGoalRepository
import com.finanzasclaras.app.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

data class AnalysisUiState(
    val baseCurrency: String = "DOP",
    val monthlySummary: List<MonthlySummary> = emptyList(),
    val currentMonthIncome: Double = 0.0,
    val currentMonthExpense: Double = 0.0,
    val previousMonthIncome: Double = 0.0,
    val previousMonthExpense: Double = 0.0,
    val expenseByCategory: List<CategoryExpense> = emptyList(),
    val categories: Map<String, Category> = emptyMap(),
    val totalSaved: Double = 0.0,
    val suggestions: List<String> = emptyList(),
    val isLoading: Boolean = true
)

class AnalysisViewModel constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val savingGoalRepository: SavingGoalRepository,
    private val investmentRepository: InvestmentRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(AnalysisUiState())
    val state: StateFlow<AnalysisUiState> = _state

    init {
        loadAnalysis()
    }

    fun loadAnalysis() {
        viewModelScope.launch {
            val prefs = userPreferences.preferences.first()
            val currency = prefs.baseCurrency

            val startOfMonth = DateUtils.toEpochMillis(DateUtils.startOfMonth())
            val endOfMonth = DateUtils.toEpochMillisEnd(DateUtils.endOfMonth())

            val prevStart = DateUtils.toEpochMillis(DateUtils.startOfPreviousMonth())
            val prevEnd = DateUtils.toEpochMillisEnd(DateUtils.endOfPreviousMonth())


            val startOfYear = DateUtils.toEpochMillis(DateUtils.startOfYear())
            val endOfYear = DateUtils.toEpochMillisEnd(DateUtils.endOfYear())

            kotlinx.coroutines.flow.combine(
                transactionRepository.getAll(),
                categoryRepository.getAll(),
                savingGoalRepository.getAll()
            ) { txs, cats, goals ->
                val currIncome = transactionRepository.getTotalIncome(startOfMonth, endOfMonth)
                val currExpense = transactionRepository.getTotalExpense(startOfMonth, endOfMonth)
                val prevIncome = transactionRepository.getTotalIncome(prevStart, prevEnd)
                val prevExpense = transactionRepository.getTotalExpense(prevStart, prevEnd)
                val saved = savingGoalRepository.getTotalSaved()

                // Suggestions
                val suggestions = mutableListOf<String>()
                if (currExpense > currIncome && currExpense > 0) {
                    suggestions.add("Tus gastos superan tus ingresos este mes. Revisa tus gastos.")
                }
                if (prevExpense > 0) {
                    val diff = ((currExpense - prevExpense) / prevExpense) * 100
                    if (diff > 10) {
                        suggestions.add("Gastaste ${kotlin.math.round(diff).toLong()}% más que el mes anterior.")
                    } else if (diff < -10) {
                        suggestions.add("Redujiste tus gastos en ${kotlin.math.round(kotlin.math.abs(diff)).toLong()}%. ¡Bien hecho!")
                    }
                }

                AnalysisUiState(
                    baseCurrency = currency,
                    currentMonthIncome = CurrencyUtils.round(currIncome),
                    currentMonthExpense = CurrencyUtils.round(currExpense),
                    previousMonthIncome = CurrencyUtils.round(prevIncome),
                    previousMonthExpense = CurrencyUtils.round(prevExpense),
                    totalSaved = CurrencyUtils.round(saved),
                    suggestions = suggestions,
                    categories = cats.associateBy { it.id },
                    isLoading = false
                )
            }.collect { newState ->
                _state.value = newState
            }
        }
    }
}
