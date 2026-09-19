package com.finanzasclaras.app.presentation.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzasclaras.app.core.local.UserPreferences
import com.finanzasclaras.app.core.util.CurrencyUtils
import com.finanzasclaras.app.core.util.DateUtils
import com.finanzasclaras.app.data.local.dao.MonthlySummary
import com.finanzasclaras.app.domain.model.Category
import com.finanzasclaras.app.domain.repository.CategoryRepository
import com.finanzasclaras.app.domain.repository.InvestmentRepository
import com.finanzasclaras.app.domain.repository.SavingGoalRepository
import com.finanzasclaras.app.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class CategoryExpenseDetail(
    val categoryId: String,
    val name: String,
    val icon: String,
    val color: Int,
    val amount: Double,
    val percentageOfTotal: Double,
    val previousAmount: Double,
    val diffAmount: Double,
    val diffPercentage: Double?
)

data class MonthlyBarItem(
    val monthName: String,
    val income: Double,
    val expense: Double
)

data class AnalysisUiState(
    val baseCurrency: String = "DOP",
    val currentMonthIncome: Double = 0.0,
    val currentMonthExpense: Double = 0.0,
    val previousMonthIncome: Double = 0.0,
    val previousMonthExpense: Double = 0.0,
    val netBalance: Double = 0.0,
    val savingsRate: Double = 0.0,
    val topCategories: List<CategoryExpenseDetail> = emptyList(),
    val highestCategory: CategoryExpenseDetail? = null,
    val monthlyBars: List<MonthlyBarItem> = emptyList(),
    val suggestions: List<String> = emptyList(),
    val totalSaved: Double = 0.0,
    val isLoading: Boolean = true
)

class AnalysisViewModel(
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

            combine(
                transactionRepository.getExpenseByCategory(startOfMonth, endOfMonth),
                transactionRepository.getExpenseByCategory(prevStart, prevEnd),
                categoryRepository.getAll(),
                savingGoalRepository.getAll(),
                transactionRepository.getMonthlySummary(startOfYear, endOfYear)
            ) { currCatExpenses, prevCatExpenses, categories, goals, monthlySummaries ->
                val currIncome = transactionRepository.getTotalIncome(startOfMonth, endOfMonth)
                val currExpense = transactionRepository.getTotalExpense(startOfMonth, endOfMonth)
                val prevIncome = transactionRepository.getTotalIncome(prevStart, prevEnd)
                val prevExpense = transactionRepository.getTotalExpense(prevStart, prevEnd)
                val saved = savingGoalRepository.getTotalSaved()

                val catsMap = categories.associateBy { it.id }
                val prevMap = prevCatExpenses.associate { it.categoryId to it.total }

                val details = currCatExpenses.map { ce ->
                    val cat = catsMap[ce.categoryId]
                    val catName = cat?.name ?: "Otros gastos"
                    val catIcon = cat?.icon ?: "category"
                    val catColor = cat?.color ?: 0xFF9E9E9E.toInt()
                    val pct = if (currExpense > 0) (ce.total / currExpense) * 100.0 else 0.0
                    val prevTotal = prevMap[ce.categoryId] ?: 0.0
                    val diff = ce.total - prevTotal
                    val diffPct = if (prevTotal > 0) (diff / prevTotal) * 100.0 else null

                    CategoryExpenseDetail(
                        categoryId = ce.categoryId,
                        name = catName,
                        icon = catIcon,
                        color = catColor,
                        amount = CurrencyUtils.round(ce.total),
                        percentageOfTotal = CurrencyUtils.round(pct),
                        previousAmount = CurrencyUtils.round(prevTotal),
                        diffAmount = CurrencyUtils.round(diff),
                        diffPercentage = diffPct?.let { CurrencyUtils.round(it) }
                    )
                }.sortedByDescending { it.amount }

                val highest = details.firstOrNull()

                // Suggestions
                val suggestions = mutableListOf<String>()
                if (highest != null && currExpense > 0) {
                    suggestions.add(
                        "Tu mayor gasto este mes es ${highest.name} con ${CurrencyUtils.format(highest.amount, currency)} (${highest.percentageOfTotal.toLong()}% de todos tus gastos)."
                    )
                }

                if (currExpense > currIncome && currIncome > 0) {
                    val deficit = currExpense - currIncome
                    suggestions.add("Tus gastos superan tus ingresos por ${CurrencyUtils.format(deficit, currency)}. Considera recortar gastos en categorías no prioritarias.")
                } else if (currIncome > 0) {
                    val rate = ((currIncome - currExpense) / currIncome) * 100
                    if (rate >= 20) {
                        suggestions.add("¡Excelente ritmo de ahorro! Estás reservando el ${rate.toLong()}% de tus ingresos este mes, cumpliendo la meta 50/30/20.")
                    }
                }

                if (prevExpense > 0) {
                    val diff = ((currExpense - prevExpense) / prevExpense) * 100
                    if (diff > 10) {
                        suggestions.add("Tus gastos aumentaron un ${diff.toLong()}% respecto al mes anterior.")
                    } else if (diff < -5) {
                        suggestions.add("¡Buen trabajo! Redujiste tus gastos un ${kotlin.math.abs(diff).toLong()}% respecto al mes pasado.")
                    }
                }

                val monthNamesShort = listOf("", "Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic")
                val bars = monthlySummaries.takeLast(6).map { s ->
                    MonthlyBarItem(
                        monthName = monthNamesShort.getOrElse(s.month) { "M${s.month}" },
                        income = CurrencyUtils.round(s.income),
                        expense = CurrencyUtils.round(s.expense)
                    )
                }

                val net = currIncome - currExpense
                val sRate = if (currIncome > 0) (net / currIncome) * 100.0 else 0.0

                AnalysisUiState(
                    baseCurrency = currency,
                    currentMonthIncome = CurrencyUtils.round(currIncome),
                    currentMonthExpense = CurrencyUtils.round(currExpense),
                    previousMonthIncome = CurrencyUtils.round(prevIncome),
                    previousMonthExpense = CurrencyUtils.round(prevExpense),
                    netBalance = CurrencyUtils.round(net),
                    savingsRate = CurrencyUtils.round(sRate),
                    topCategories = details,
                    highestCategory = highest,
                    monthlyBars = bars,
                    suggestions = suggestions,
                    totalSaved = CurrencyUtils.round(saved),
                    isLoading = false
                )
            }.collect { newState ->
                _state.value = newState
            }
        }
    }
}
