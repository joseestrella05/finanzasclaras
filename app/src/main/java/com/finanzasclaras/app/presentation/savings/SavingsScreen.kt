package com.finanzasclaras.app.presentation.savings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.finanzasclaras.app.core.common.Green500
import com.finanzasclaras.app.core.util.CurrencyUtils
import com.finanzasclaras.app.domain.model.SavingGoal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SavingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var goalName by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Metas de ahorro") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::showCreateDialog) {
                Icon(Icons.Default.Add, contentDescription = "Nueva meta")
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Cargando...")
            }
        } else if (state.goals.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Savings,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No hay metas de ahorro",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = viewModel::showCreateDialog) {
                        Text("Crear primera meta")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.goals) { goal ->
                    SavingGoalCard(
                        goal = goal,
                        currency = state.baseCurrency,
                        onContribute = { viewModel.showContributeDialog(goal) },
                        onDelete = { viewModel.deleteGoal(goal.id) }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        // Create dialog
        if (state.showCreateDialog) {
            AlertDialog(
                onDismissRequest = viewModel::hideCreateDialog,
                title = { Text("Nueva meta") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = goalName,
                            onValueChange = { goalName = it },
                            label = { Text("Nombre") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = targetAmount,
                            onValueChange = { targetAmount = it },
                            label = { Text("Monto objetivo") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                        if (state.error != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { viewModel.createGoal(goalName, targetAmount, null) }) {
                        Text("Crear")
                    }
                },
                dismissButton = {
                    TextButton(onClick = viewModel::hideCreateDialog) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // Contribute dialog
        state.showContributeDialog?.let { goal ->
            var amount by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = viewModel::hideContributeDialog,
                title = { Text("Aportar a ${goal.name}") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            label = { Text("Monto") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                        if (state.error != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { viewModel.addContribution(goal.id, amount) }) {
                        Text("Aportar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = viewModel::hideContributeDialog) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
private fun SavingGoalCard(
    goal: SavingGoal,
    currency: String,
    onContribute: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (goal.completed) Green500.copy(alpha = 0.1f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = goal.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                if (goal.completed) {
                    Text(
                        text = "Completada",
                        style = MaterialTheme.typography.bodySmall,
                        color = Green500,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { goal.progress },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = if (goal.completed) Green500 else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = CurrencyUtils.format(goal.currentAmount, currency),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "de ${CurrencyUtils.format(goal.targetAmount, currency)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (goal.deadlineDate != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Fecha límite: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(goal.deadlineDate))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            if (!goal.completed) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onContribute, modifier = Modifier.fillMaxWidth()) {
                    Text("Aportar")
                }
            }
        }
    }
}
