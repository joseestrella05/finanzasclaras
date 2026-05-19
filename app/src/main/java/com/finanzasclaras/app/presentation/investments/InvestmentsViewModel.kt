package com.finanzasclaras.app.presentation.investments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzasclaras.app.core.local.UserPreferences
import com.finanzasclaras.app.core.util.CurrencyUtils
import com.finanzasclaras.app.domain.model.Investment
import com.finanzasclaras.app.domain.repository.InvestmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class InvestmentsUiState(
    val investments: List<Investment> = emptyList(),
    val baseCurrency: String = "DOP",
    val totalInvested: Double = 0.0,
    val totalCurrentValue: Double = 0.0,
    val totalProfitLoss: Double = 0.0,
    val showCreateDialog: Boolean = false,
    val editingInvestment: Investment? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class InvestmentsViewModel @Inject constructor(
    private val investmentRepository: InvestmentRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(InvestmentsUiState())
    val state: StateFlow<InvestmentsUiState> = _state

    init {
        viewModelScope.launch {
            val prefs = userPreferences.preferences.first()
            _state.value = _state.value.copy(baseCurrency = prefs.baseCurrency)
            loadInvestments()
        }
    }

    private fun loadInvestments() {
        viewModelScope.launch {
            investmentRepository.getAll().collect { invs ->
                val invested = investmentRepository.getTotalInvested()
                val currentValue = investmentRepository.getTotalCurrentValue()
                _state.value = _state.value.copy(
                    investments = invs,
                    totalInvested = CurrencyUtils.round(invested),
                    totalCurrentValue = CurrencyUtils.round(currentValue),
                    totalProfitLoss = CurrencyUtils.round(currentValue - invested),
                    isLoading = false
                )
            }
        }
    }

    fun showCreateDialog() { _state.value = _state.value.copy(showCreateDialog = true, editingInvestment = null, error = null) }
    fun showEditDialog(investment: Investment) { _state.value = _state.value.copy(showCreateDialog = true, editingInvestment = investment, error = null) }
    fun hideCreateDialog() { _state.value = _state.value.copy(showCreateDialog = false, editingInvestment = null, error = null) }

    fun saveInvestment(name: String, type: String, amount: String, currentValue: String) {
        if (name.isBlank() || type.isBlank()) {
            _state.value = _state.value.copy(error = "El nombre y el tipo son obligatorios.")
            return
        }

        val a = amount.toDoubleOrNull()
        if (a == null || a <= 0) {
            _state.value = _state.value.copy(error = "El monto invertido debe ser un número mayor a 0.")
            return
        }

        val cv = currentValue.toDoubleOrNull() ?: a

        viewModelScope.launch {
            val editing = _state.value.editingInvestment
            if (editing != null) {
                val updated = editing.copy(
                    name = name,
                    type = type,
                    amountInvested = a,
                    currentValue = cv,
                    updatedAt = System.currentTimeMillis()
                )
                investmentRepository.update(updated)
            } else {
                val inv = Investment(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    type = type,
                    amountInvested = a,
                    currentValue = cv,
                    currency = _state.value.baseCurrency,
                    purchaseDate = System.currentTimeMillis(),
                    notes = "",
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                investmentRepository.create(inv)
            }
            _state.value = _state.value.copy(showCreateDialog = false, editingInvestment = null, error = null)
        }
    }

    fun deleteInvestment(id: String) {
        viewModelScope.launch { investmentRepository.delete(id) }
    }
}
