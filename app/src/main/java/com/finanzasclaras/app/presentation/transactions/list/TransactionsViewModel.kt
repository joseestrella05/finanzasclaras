package com.finanzasclaras.app.presentation.transactions.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzasclaras.app.core.util.CurrencyUtils
import com.finanzasclaras.app.core.util.DateUtils
import com.finanzasclaras.app.domain.model.Category
import com.finanzasclaras.app.domain.model.Transaction
import com.finanzasclaras.app.domain.model.TransactionType
import com.finanzasclaras.app.domain.repository.CategoryRepository
import com.finanzasclaras.app.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TransactionFilter {
    ALL, INCOME, EXPENSE
}

enum class DateFilter {
    DAY, WEEK, MONTH, YEAR, ALL
}

data class TransactionsUiState(
    val transactions: List<Transaction> = emptyList(),
    val categories: Map<String, Category> = emptyMap(),
    val filter: TransactionFilter = TransactionFilter.ALL,
    val dateFilter: DateFilter = DateFilter.MONTH,
    val isLoading: Boolean = true
)

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionsUiState())
    val state: StateFlow<TransactionsUiState> = _state

    private var allCategories: List<Category> = emptyList()

    init {
        loadCategories()
        loadTransactions()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getAll().collect { cats ->
                allCategories = cats
                _state.value = _state.value.copy(
                    categories = cats.associateBy { it.id }
                )
            }
        }
    }

    fun loadTransactions() {
        viewModelScope.launch {
            val (start, end) = getDateRange()
            val flow = when (_state.value.filter) {
                TransactionFilter.ALL -> transactionRepository.getByDateRange(start, end)
                TransactionFilter.INCOME -> transactionRepository.getByTypeAndDateRange(TransactionType.INCOME, start, end)
                TransactionFilter.EXPENSE -> transactionRepository.getByTypeAndDateRange(TransactionType.EXPENSE, start, end)
            }
            flow.collect { txList ->
                _state.value = _state.value.copy(
                    transactions = txList,
                    isLoading = false
                )
            }
        }
    }

    fun setFilter(filter: TransactionFilter) {
        _state.value = _state.value.copy(filter = filter)
        loadTransactions()
    }

    fun setDateFilter(dateFilter: DateFilter) {
        _state.value = _state.value.copy(dateFilter = dateFilter)
        loadTransactions()
    }

    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            transactionRepository.softDelete(id)
        }
    }

    fun getCategoryName(categoryId: String): String {
        return allCategories.find { it.id == categoryId }?.name ?: "General"
    }

    private fun getDateRange(): Pair<Long, Long> {
        val now = System.currentTimeMillis()
        return when (_state.value.dateFilter) {
            DateFilter.DAY -> {
                val start = DateUtils.toEpochMillis(DateUtils.today())
                val end = DateUtils.toEpochMillisEnd(DateUtils.today())
                start to end
            }
            DateFilter.WEEK -> {
                val start = DateUtils.toEpochMillis(DateUtils.startOfWeek())
                val end = DateUtils.toEpochMillisEnd(DateUtils.endOfWeek())
                start to end
            }
            DateFilter.MONTH -> {
                val start = DateUtils.toEpochMillis(DateUtils.startOfMonth())
                val end = DateUtils.toEpochMillisEnd(DateUtils.endOfMonth())
                start to end
            }
            DateFilter.YEAR -> {
                val start = DateUtils.toEpochMillis(DateUtils.startOfYear())
                val end = DateUtils.toEpochMillisEnd(DateUtils.endOfYear())
                start to end
            }
            DateFilter.ALL -> 0L to Long.MAX_VALUE
        }
    }
}
