package com.finanzasclaras.app.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzasclaras.app.core.local.UserPreferences
import com.finanzasclaras.app.core.util.CurrencyUtils
import com.finanzasclaras.app.core.util.DateUtils
import com.finanzasclaras.app.data.remote.firebase.FirebaseSyncManager
import com.finanzasclaras.app.domain.repository.CategoryRepository
import com.finanzasclaras.app.domain.repository.InvestmentRepository
import com.finanzasclaras.app.domain.repository.SavingGoalRepository
import com.finanzasclaras.app.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class DashboardUiState(
    val baseCurrency: String = "DOP",
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val totalSaved: Double = 0.0,
    val totalInvested: Double = 0.0,
    val totalCurrentValue: Double = 0.0,
    val recentTransactions: List<com.finanzasclaras.app.domain.model.Transaction> = emptyList(),
    val categoryNames: Map<String, String> = emptyMap(),
    val categoriesMap: Map<String, com.finanzasclaras.app.domain.model.Category> = emptyMap(),
    val isLoading: Boolean = true,
    val userName: String = ""
)

class DashboardViewModel constructor(
    private val transactionRepository: TransactionRepository,
    private val savingGoalRepository: SavingGoalRepository,
    private val investmentRepository: InvestmentRepository,
    private val categoryRepository: CategoryRepository,
    private val userPreferences: UserPreferences,
    private val syncManager: FirebaseSyncManager
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state

    init {
        loadData()
        sync()
    }

    fun sync() {
        viewModelScope.launch {
            try {
                syncManager.syncAll()
            } catch (_: Throwable) {}
        }
    }

    fun loadData() {
        viewModelScope.launch {
            val prefs = userPreferences.preferences.first()
            val currency = prefs.baseCurrency

            val startMillis = DateUtils.toEpochMillis(DateUtils.startOfMonth())
            val endMillis = DateUtils.toEpochMillisEnd(DateUtils.endOfMonth())

            combine(
                transactionRepository.getAll(),
                categoryRepository.getAll(),
                savingGoalRepository.getAll(),
                investmentRepository.getAll()
            ) { transactions, categories, goals, investments ->
                val income = transactionRepository.getTotalIncome(startMillis, endMillis)
                val expense = transactionRepository.getTotalExpense(startMillis, endMillis)
                val saved = savingGoalRepository.getTotalSaved()
                val invested = investmentRepository.getTotalInvested()
                val currentValue = investmentRepository.getTotalCurrentValue()

                DashboardUiState(
                    baseCurrency = currency,
                    totalIncome = CurrencyUtils.round(income),
                    totalExpense = CurrencyUtils.round(expense),
                    balance = CurrencyUtils.round(income - expense),
                    totalSaved = CurrencyUtils.round(saved),
                    totalInvested = CurrencyUtils.round(invested),
                    totalCurrentValue = CurrencyUtils.round(currentValue),
                    recentTransactions = transactions.take(6),
                    categoryNames = categories.associate { it.id to it.name },
                    categoriesMap = categories.associateBy { it.id },
                    isLoading = false,
                    userName = UserPreferences.formatDisplayName(prefs.userName).ifBlank { if (prefs.isLoggedIn) "Jose Gabriel Estrella" else "" }
                )
            }.collect { newState ->
                _state.value = newState
            }
        }
    }
}
