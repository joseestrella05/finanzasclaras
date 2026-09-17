package com.finanzasclaras.app.presentation.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import com.finanzasclaras.app.core.util.IconUtils
import com.finanzasclaras.app.domain.model.Category
import com.finanzasclaras.app.domain.model.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    onNavigateBack: () -> Unit,
    viewModel: CategoriesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }
    
    var name by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("category") }
    var selectedColor by remember { mutableStateOf(0xFF2196F3.toInt()) }

    val availableColors = listOf(
        0xFFF44336.toInt(), // Red
        0xFFE91E63.toInt(), // Pink
        0xFF9C27B0.toInt(), // Purple
        0xFF673AB7.toInt(), // Deep Purple
        0xFF3F51B5.toInt(), // Indigo
        0xFF2196F3.toInt(), // Blue
        0xFF03A9F4.toInt(), // Light Blue
        0xFF00BCD4.toInt(), // Cyan
        0xFF009688.toInt(), // Teal
        0xFF4CAF50.toInt(), // Green
        0xFF8BC34A.toInt(), // Light Green
        0xFFCDDC39.toInt(), // Lime
        0xFFFFEB3B.toInt(), // Yellow
        0xFFFFC107.toInt(), // Amber
        0xFFFF9800.toInt(), // Orange
        0xFFFF5722.toInt(), // Deep Orange
        0xFF795548.toInt(), // Brown
        0xFF9E9E9E.toInt(), // Grey
        0xFF607D8B.toInt()  // Blue Grey
    )

    val availableIcons = listOf(
        "category", "shopping_cart", "restaurant", "directions_car",
        "local_hospital", "school", "home", "bolt", "checkroom",
        "sports_esports", "pets", "flight", "work", "trending_up",
        "attach_money", "credit_card", "agriculture"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Categorías") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::showCreateDialog) {
                Icon(Icons.Default.Add, contentDescription = "Nueva categoría")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Gastos") }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Ingresos") }
                )
            }

            val listToShow = if (selectedTabIndex == 0) state.expenseCategories else state.incomeCategories

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listToShow) { category ->
                    CategoryItem(
                        category = category,
                        onDelete = {
                            if (!category.isDefault) viewModel.deleteCategory(category.id)
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        if (state.showCreateDialog) {
            AlertDialog(
                onDismissRequest = viewModel::hideCreateDialog,
                title = { Text("Nueva Categoría") },
                text = {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = state.selectedType == TransactionType.EXPENSE,
                                onClick = { viewModel.setSelectedType(TransactionType.EXPENSE) },
                                label = { Text("Gasto") },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = state.selectedType == TransactionType.INCOME,
                                onClick = { viewModel.setSelectedType(TransactionType.INCOME) },
                                label = { Text("Ingreso") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Nombre") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Icono", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(availableIcons) { iconName ->
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(if (selectedIcon == iconName) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                        .clickable { selectedIcon = iconName },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = IconUtils.getIcon(iconName),
                                        contentDescription = null,
                                        tint = if (selectedIcon == iconName) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Color", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(availableColors) { color ->
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color(color))
                                        .clickable { selectedColor = color },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (selectedColor == color) {
                                        Icon(
                                            Icons.Default.Add, // Placeholder for check
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        if (state.error != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        viewModel.createCategory(name, selectedIcon, selectedColor)
                        if (name.isNotBlank()) name = "" // Reset only if success
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
private fun CategoryItem(category: Category, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(category.color).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = IconUtils.getIcon(category.icon),
                        contentDescription = null,
                        tint = Color(category.color)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = category.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                    if (category.isDefault) {
                        Text(text = "Por defecto", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    } else {
                        Text(text = "Personalizada", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
            if (!category.isDefault) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
