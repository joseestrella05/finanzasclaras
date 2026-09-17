package com.finanzasclaras.app.presentation.transactions.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzasclaras.app.core.common.Constants
import com.finanzasclaras.app.core.local.UserPreferences
import com.finanzasclaras.app.core.util.CurrencyUtils
import com.finanzasclaras.app.domain.model.Category
import com.finanzasclaras.app.domain.model.Transaction
import com.finanzasclaras.app.domain.model.TransactionType
import com.finanzasclaras.app.domain.repository.CategoryRepository
import com.finanzasclaras.app.domain.repository.TransactionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class AddTransactionUiState(
    val amount: String = "",
    val selectedCategory: Category? = null,
    val categories: List<Category> = emptyList(),
    val note: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val currency: String = "DOP",
    val date: Long = kotlinx.datetime.Clock.System.now().toEpochMilliseconds(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class AddTransactionViewModel constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(AddTransactionUiState())
    val state: StateFlow<AddTransactionUiState> = _state

    private var editingTransactionId: String? = null
    private var categoryJob: Job? = null

    init {
        viewModelScope.launch {
            categoryRepository.seedDefaultCategories()
            val prefs = userPreferences.preferences.first()
            _state.value = _state.value.copy(currency = prefs.baseCurrency)
            observeCategories(_state.value.type)
        }
    }

    fun loadTransaction(id: String) {
        editingTransactionId = id
        viewModelScope.launch {
            val tx = transactionRepository.getById(id) ?: return@launch
            val cat = categoryRepository.getById(tx.categoryId)
            _state.value = _state.value.copy(
                amount = if (tx.amount == tx.amount.toLong().toDouble())
                    tx.amount.toLong().toString() else tx.amount.toString(),
                selectedCategory = cat,
                note = tx.note,
                type = tx.type,
                currency = tx.currency,
                date = tx.date
            )
            observeCategories(tx.type)
        }
    }

    private fun observeCategories(type: TransactionType) {
        categoryJob?.cancel()
        categoryJob = viewModelScope.launch {
            categoryRepository.getByType(type).collect { cats ->
                val currentSelected = _state.value.selectedCategory
                val newSelected = if (currentSelected != null && cats.any { it.id == currentSelected.id }) {
                    currentSelected
                } else {
                    cats.firstOrNull()
                }
                _state.value = _state.value.copy(
                    categories = cats,
                    selectedCategory = newSelected,
                    error = if (newSelected != null && _state.value.error == "Selecciona una categoría") null else _state.value.error
                )
            }
        }
    }

    fun setAmount(amount: String) {
        _state.value = _state.value.copy(amount = amount, error = null)
    }

    fun addQuickAmount(add: Double) {
        val current = _state.value.amount.toDoubleOrNull() ?: 0.0
        val newAmount = current + add
        val text = if (newAmount == newAmount.toLong().toDouble()) newAmount.toLong().toString() else newAmount.toString()
        setAmount(text)
    }

    fun setCategory(category: Category) {
        _state.value = _state.value.copy(selectedCategory = category, error = null)
    }

    fun setNote(note: String) {
        _state.value = _state.value.copy(note = note)
    }

    fun setType(type: TransactionType) {
        if (_state.value.type == type) return
        _state.value = _state.value.copy(type = type, selectedCategory = null)
        observeCategories(type)
    }

    fun setCurrency(currency: String) {
        _state.value = _state.value.copy(currency = currency)
    }

    fun setDate(date: Long) {
        _state.value = _state.value.copy(date = date)
    }

    fun save() {
        val s = _state.value
        val amount = s.amount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _state.value = s.copy(error = "Ingresa un monto válido")
            return
        }
        if (s.selectedCategory == null) {
            _state.value = s.copy(error = "Selecciona una categoría")
            return
        }

        viewModelScope.launch {
            _state.value = s.copy(isSaving = true)
            val now = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
            val amountInBase = CurrencyUtils.convert(amount, s.currency, s.currency)

            val tx = Transaction(
                id = editingTransactionId ?: com.finanzasclaras.app.core.util.IdUtils.randomId(),
                categoryId = s.selectedCategory.id,
                amount = CurrencyUtils.round(amount),
                currency = s.currency,
                amountInBase = CurrencyUtils.round(amountInBase),
                type = s.type,
                note = s.note,
                date = s.date,
                createdAt = if (editingTransactionId != null) {
                    transactionRepository.getById(editingTransactionId!!)?.createdAt ?: now
                } else now,
                updatedAt = now
            )

            if (editingTransactionId != null) {
                transactionRepository.update(tx)
            } else {
                transactionRepository.create(tx)
            }
            _state.value = _state.value.copy(isSaving = false, isSuccess = true)
        }
    }
}
