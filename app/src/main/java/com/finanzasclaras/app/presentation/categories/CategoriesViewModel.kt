package com.finanzasclaras.app.presentation.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finanzasclaras.app.domain.model.Category
import com.finanzasclaras.app.domain.model.TransactionType
import com.finanzasclaras.app.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class CategoriesUiState(
    val incomeCategories: List<Category> = emptyList(),
    val expenseCategories: List<Category> = emptyList(),
    val showCreateDialog: Boolean = false,
    val selectedType: TransactionType = TransactionType.EXPENSE,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CategoriesUiState())
    val state: StateFlow<CategoriesUiState> = _state

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getAll().collect { categories ->
                _state.value = _state.value.copy(
                    incomeCategories = categories.filter { it.type == TransactionType.INCOME },
                    expenseCategories = categories.filter { it.type == TransactionType.EXPENSE },
                    isLoading = false
                )
            }
        }
    }

    fun showCreateDialog() {
        _state.value = _state.value.copy(showCreateDialog = true, error = null)
    }

    fun hideCreateDialog() {
        _state.value = _state.value.copy(showCreateDialog = false, error = null)
    }

    fun setSelectedType(type: TransactionType) {
        _state.value = _state.value.copy(selectedType = type)
    }

    fun createCategory(name: String, icon: String, color: Int) {
        if (name.isBlank()) {
            _state.value = _state.value.copy(error = "El nombre de la categoría es obligatorio.")
            return
        }
        
        viewModelScope.launch {
            val category = Category(
                id = UUID.randomUUID().toString(),
                name = name,
                nameEn = name, // Use same for custom
                icon = icon,
                type = _state.value.selectedType,
                color = color,
                isDefault = false
            )
            categoryRepository.create(category)
            _state.value = _state.value.copy(showCreateDialog = false, error = null)
        }
    }

    fun deleteCategory(id: String) {
        viewModelScope.launch {
            categoryRepository.delete(id)
        }
    }
}
