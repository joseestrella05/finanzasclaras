package com.finanzasclaras.app.presentation.investments

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import org.koin.compose.viewmodel.koinViewModel
import com.finanzasclaras.app.core.common.Green500
import com.finanzasclaras.app.core.common.Red500
import com.finanzasclaras.app.core.util.CurrencyUtils
import com.finanzasclaras.app.domain.model.Investment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestmentsScreen(
    onNavigateBack: () -> Unit,
    viewModel: InvestmentsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var currentValue by remember { mutableStateOf("") }

    LaunchedEffect(state.editingInvestment, state.showCreateDialog) {
        if (state.showCreateDialog) {
            val editing = state.editingInvestment
            if (editing != null) {
                name = editing.name
                type = editing.type
                amount = editing.amountInvested.toString()
                currentValue = editing.currentValue.toString()
            } else {
                name = ""
                type = ""
                amount = ""
                currentValue = ""
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inversiones") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::showCreateDialog) {
                Icon(Icons.Default.Add, contentDescription = "Nueva inversión")
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Cargando...")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Portfolio summary
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (state.totalProfitLoss >= 0)
                                Green500.copy(alpha = 0.1f)
                            else Red500.copy(alpha = 0.1f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Portafolio total",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = CurrencyUtils.format(state.totalCurrentValue, state.baseCurrency),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Invertido: ${CurrencyUtils.format(state.totalInvested, state.baseCurrency)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Ganancia/Pérdida: ${CurrencyUtils.format(state.totalProfitLoss, state.baseCurrency)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (state.totalProfitLoss >= 0) Green500 else Red500
                            )
                        }
                    }
                }

                if (state.investments.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.outline
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No hay inversiones", color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                }

                items(state.investments) { inv ->
                    InvestmentCard(
                        investment = inv,
                        currency = state.baseCurrency,
                        onEdit = { viewModel.showEditDialog(inv) }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        if (state.showCreateDialog) {
            AlertDialog(
                onDismissRequest = viewModel::hideCreateDialog,
                title = { Text(if (state.editingInvestment != null) "Editar inversión" else "Nueva inversión") },
                text = {
                    Column {
                        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Tipo (ej. Bienes Raíces, Negocio)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Monto invertido") }, singleLine = true, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedTextField(value = currentValue, onValueChange = { currentValue = it }, label = { Text("Valor actual (opcional)") }, singleLine = true, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))

                        if (state.error != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        viewModel.saveInvestment(name, type, amount, currentValue)
                    }) { Text("Guardar") }
                },
                dismissButton = {
                    TextButton(onClick = viewModel::hideCreateDialog) { Text("Cancelar") }
                }
            )
        }
    }
}

@Composable
private fun InvestmentCard(investment: Investment, currency: String, onEdit: () -> Unit) {
    val dateStr = com.finanzasclaras.app.core.util.DateUtils.formatDisplay(investment.purchaseDate)


    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = investment.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        IconButton(onClick = onEdit, modifier = Modifier.size(28.dp).padding(start = 4.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Text(text = investment.type, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = CurrencyUtils.format(investment.currentValue, currency),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${com.finanzasclaras.app.core.util.CurrencyUtils.round(investment.profitLossPercentage).toString()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (investment.isProfitable) Green500 else Red500
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Comprado: $dateStr", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
            Text(
                text = "Invertido: ${CurrencyUtils.format(investment.amountInvested, currency)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
