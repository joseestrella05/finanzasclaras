package com.finanzasclaras.app.presentation.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.finanzasclaras.app.core.local.UserPreferences
import androidx.hilt.navigation.compose.hiltViewModel

data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val description: String
)

private val pages = listOf(
    OnboardingPage(
        icon = Icons.Default.AccountBalance,
        title = "Controla tus finanzas",
        description = "Registra todos tus ingresos y gastos de forma sencilla. Mantén un control claro de tu dinero."
    ),
    OnboardingPage(
        icon = Icons.Default.Savings,
        title = "Ahorra e invierte",
        description = "Establece metas de ahorro, registra tus inversiones y mira crecer tu patrimonio."
    ),
    OnboardingPage(
        icon = Icons.Default.BarChart,
        title = "Analiza y mejora",
        description = "Visualiza gráficos detallados, compara meses y recibe sugerencias inteligentes para ahorrar más."
    )
)

@Composable
fun OnboardingScreen(
    onCompleted: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    var currentPage by remember { mutableStateOf(0) }
    val page = pages[currentPage]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        AnimatedContent(targetState = currentPage) { pageIndex ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = pages[pageIndex].icon,
                    contentDescription = null,
                    modifier = Modifier.size(120.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = pages[pageIndex].title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = pages[pageIndex].description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            pages.indices.forEach { index ->
                Box(
                    modifier = Modifier
                        .size(if (index == currentPage) 12.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (index == currentPage) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (currentPage < pages.size - 1) {
                    currentPage++
                } else {
                    viewModel.completeOnboarding()
                    onCompleted()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (currentPage < pages.size - 1) "Siguiente" else "Comenzar"
            )
        }

        if (currentPage < pages.size - 1) {
            TextButton(onClick = {
                viewModel.completeOnboarding()
                onCompleted()
            }) {
                Text("Saltar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
