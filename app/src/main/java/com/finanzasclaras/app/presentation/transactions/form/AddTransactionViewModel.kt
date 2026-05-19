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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class AddTransactionUiState(
    val amount: String = "",
    val selectedCategory: Category? = null,
    val categories: List<Category> = emptyList(),
    val note: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val currency: String = "DOP",
    val date: Long = System.currentTimeMillis(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(AddTransactionUiState())
    val state: StateFlow<AddTransactionUiState> = _state

    private var editingTransactionId: String? = null

    init {
        viewModelScope.launch {
            val prefs = userPreferences.preferences.first()
            _state.value = _state.value.copy(currency = prefs.baseCurrency)
            loadCategories()
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
        }
    }

    private suspend fun loadCategories() {
        categoryRepository.getByType(_state.value.type).collect { cats ->
            _state.value = _state.value.copy(
                categories = cats,
                selectedCategory = _state.value.selectedCategory ?: cats.firstOrNull()
            )
        }
    }

    fun setAmount(amount: String) {
        _state.value = _state.value.copy(amount = amount, error = null)
    }

    fun setCategory(category: Category) {
        _state.value = _state.value.copy(selectedCategory = category)
    }

    fun setNote(note: String) {
        _state.value = _state.value.copy(note = note)
    }

    fun setType(type: TransactionType) {
        _state.value = _state.value.copy(type = type, selectedCategory = null)
        viewModelScope.launch { loadCategories() }
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
            val now = System.currentTimeMillis()
            val amountInBase = CurrencyUtils.convert(amount, s.currency, s.currency)

            val tx = Transaction(
                id = editingTransactionId ?: UUID.randomUUID().toString(),
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
