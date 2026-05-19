package com.finanzasclaras.app.presentation.analysis

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.finanzasclaras.app.core.common.Green500
import com.finanzasclaras.app.core.common.Red500
import com.finanzasclaras.app.core.util.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(
    onNavigateBack: () -> Unit,
    viewModel: AnalysisViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Análisis financiero") },
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
            // Balance neto
            BalanceCard(
                income = state.currentMonthIncome,
                expense = state.currentMonthExpense,
                currency = state.baseCurrency
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Comparación mes actual vs anterior
            Text(
                text = "Comparación mensual",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            ComparisonCard(
                currentIncome = state.currentMonthIncome,
                currentExpense = state.currentMonthExpense,
                previousIncome = state.previousMonthIncome,
                previousExpense = state.previousMonthExpense,
                currency = state.baseCurrency
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Ingresos vs Gastos chart
            Text(
                text = "Ingresos vs Gastos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            IncomeExpenseChart(
                income = state.currentMonthIncome,
                expense = state.currentMonthExpense,
                currency = state.baseCurrency
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sugerencias
            if (state.suggestions.isNotEmpty()) {
                Text(
                    text = "Sugerencias",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                state.suggestions.forEach { suggestion ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.size(12.dp))
                            Text(
                                text = suggestion,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Export button placeholder
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Download, contentDescription = null)
                    Spacer(modifier = Modifier.size(12.dp))
                    Text("Exportar reporte a PDF", style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun BalanceCard(income: Double, expense: Double, currency: String) {
    val balance = income - expense
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (balance >= 0)
                Green500.copy(alpha = 0.15f)
            else Red500.copy(alpha = 0.15f)
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Balance neto",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = CurrencyUtils.format(balance, currency),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = if (balance >= 0) Green500 else Red500
            )
        }
    }
}

@Composable
private fun ComparisonCard(
    currentIncome: Double,
    currentExpense: Double,
    previousIncome: Double,
    previousExpense: Double,
    currency: String
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Mes actual", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(CurrencyUtils.format(currentIncome - currentExpense, currency), fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Mes anterior", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(CurrencyUtils.format(previousIncome - previousExpense, currency), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun IncomeExpenseChart(
    income: Double,
    expense: Double,
    currency: String
) {
    val maxVal = maxOf(income, expense, 1.0)
    val incomeRatio = (income / maxVal).toFloat()
    val expenseRatio = (expense / maxVal).toFloat()

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                val barWidth = size.width / 4
                val maxBarHeight = size.height - 40f

                // Income bar
                drawRect(
                    color = Green500,
                    topLeft = Offset(barWidth, size.height - 20f - maxBarHeight * incomeRatio),
                    size = Size(barWidth, maxBarHeight * incomeRatio)
                )
                drawRect(
                    color = Green500.copy(alpha = 0.3f),
                    topLeft = Offset(barWidth, size.height - 20f),
                    size = Size(barWidth, 0f)
                )

                // Expense bar
                drawRect(
                    color = Red500,
                    topLeft = Offset(barWidth * 3, size.height - 20f - maxBarHeight * expenseRatio),
                    size = Size(barWidth, maxBarHeight * expenseRatio)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "Ingresos: ${CurrencyUtils.format(income, currency)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Green500,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Gastos: ${CurrencyUtils.format(expense, currency)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Red500,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
