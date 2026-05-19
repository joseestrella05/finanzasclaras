package com.finanzasclaras.app.presentation.savings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzasclaras.app.core.local.UserPreferences
import com.finanzasclaras.app.core.util.CurrencyUtils
import com.finanzasclaras.app.domain.model.SavingContribution
import com.finanzasclaras.app.domain.model.SavingGoal
import com.finanzasclaras.app.domain.repository.SavingGoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class SavingsUiState(
    val goals: List<SavingGoal> = emptyList(),
    val showCreateDialog: Boolean = false,
    val showContributeDialog: SavingGoal? = null,
    val baseCurrency: String = "DOP",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null
)

data class CreateGoalData(
    val name: String = "",
    val targetAmount: String = "",
    val deadlineDate: Long? = null
)

@HiltViewModel
class SavingsViewModel @Inject constructor(
    private val savingGoalRepository: SavingGoalRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(SavingsUiState())
    val state: StateFlow<SavingsUiState> = _state

    init {
        viewModelScope.launch {
            val prefs = userPreferences.preferences.first()
            _state.value = _state.value.copy(baseCurrency = prefs.baseCurrency)
            loadGoals()
        }
    }

    private fun loadGoals() {
        viewModelScope.launch {
            savingGoalRepository.getAll().collect { goals ->
                _state.value = _state.value.copy(goals = goals, isLoading = false)
            }
        }
    }

    fun showCreateDialog() {
        _state.value = _state.value.copy(showCreateDialog = true, error = null)
    }

    fun hideCreateDialog() {
        _state.value = _state.value.copy(showCreateDialog = false, error = null)
    }

    fun createGoal(name: String, targetAmountStr: String, deadlineDate: Long?) {
        if (name.isBlank()) {
            _state.value = _state.value.copy(error = "El nombre de la meta es obligatorio.")
            return
        }
        val amount = targetAmountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _state.value = _state.value.copy(error = "El monto objetivo debe ser mayor a 0.")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)
            val goal = SavingGoal(
                id = UUID.randomUUID().toString(),
                name = name,
                targetAmount = amount,
                currentAmount = 0.0,
                currency = _state.value.baseCurrency,
                deadlineDate = deadlineDate,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                completed = false
            )
            savingGoalRepository.create(goal)
            _state.value = _state.value.copy(
                showCreateDialog = false,
                isSaving = false,
                error = null
            )
        }
    }

    fun showContributeDialog(goal: SavingGoal) {
        _state.value = _state.value.copy(showContributeDialog = goal, error = null)
    }

    fun hideContributeDialog() {
        _state.value = _state.value.copy(showContributeDialog = null, error = null)
    }

    fun addContribution(goalId: String, amountStr: String) {
        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _state.value = _state.value.copy(error = "El monto debe ser un número mayor a 0.")
            return
        }

        viewModelScope.launch {
            val contribution = SavingContribution(
                id = UUID.randomUUID().toString(),
                goalId = goalId,
                amount = amount,
                currency = _state.value.baseCurrency,
                date = System.currentTimeMillis(),
                note = ""
            )
            savingGoalRepository.addContribution(goalId, contribution)
            _state.value = _state.value.copy(showContributeDialog = null, error = null)
        }
    }

    fun deleteGoal(id: String) {
        viewModelScope.launch {
            savingGoalRepository.delete(id)
        }
    }
}
