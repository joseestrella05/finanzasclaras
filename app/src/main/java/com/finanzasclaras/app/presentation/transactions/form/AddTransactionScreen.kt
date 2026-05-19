package com.finanzasclaras.app.presentation.transactions.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.finanzasclaras.app.domain.model.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    transactionId: String? = null,
    onNavigateBack: () -> Unit,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(transactionId) {
        if (transactionId != null) {
            viewModel.loadTransaction(transactionId)
        }
    }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (transactionId != null) "Editar transacción" else "Nueva transacción") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Type selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterChip(
                    selected = state.type == TransactionType.EXPENSE,
                    onClick = { viewModel.setType(TransactionType.EXPENSE) },
                    label = { Text("Gasto") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = state.type == TransactionType.INCOME,
                    onClick = { viewModel.setType(TransactionType.INCOME) },
                    label = { Text("Ingreso") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Amount
            OutlinedTextField(
                value = state.amount,
                onValueChange = viewModel::setAmount,
                label = { Text("Monto") },
                leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = state.error != null
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Currency selector
            Text(
                text = "Moneda",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                com.finanzasclaras.app.core.common.Constants.Currencies.SUPPORTED.take(4).forEach { currency ->
                    FilterChip(
                        selected = state.currency == currency,
                        onClick = { viewModel.setCurrency(currency) },
                        label = { Text(currency) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category
            Text(
                text = "Categoría",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.categories.take(4).forEach { category ->
                    FilterChip(
                        selected = state.selectedCategory?.id == category.id,
                        onClick = { viewModel.setCategory(category) },
                        label = { Text(category.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Note
            OutlinedTextField(
                value = state.note,
                onValueChange = viewModel::setNote,
                label = { Text("Nota (opcional)") },
                leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            if (state.error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = viewModel::save,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSaving
            ) {
                Text(
                    if (state.isSaving) "Guardando..."
                    else if (transactionId != null) "Actualizar"
                    else "Guardar"
                )
            }
        }
    }
}
