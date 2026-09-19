package com.finanzasclaras.app.domain.logic

import com.finanzasclaras.app.core.util.CurrencyUtils
import com.finanzasclaras.app.domain.model.Category

data class CategoryRecommendation(
    val categoryName: String,
    val percentageOfIncome: Double,
    val recommendedAmount: Double,
    val pillar: FinancialPillar, // NEEDS, WANTS, SAVINGS
    val advice: String
)

enum class FinancialPillar(val title: String, val recommendedPercentage: Double, val description: String) {
    NEEDS("Necesidades básicas", 50.0, "Vivienda, comida, transporte y servicios"),
    WANTS("Deseos y estilo de vida", 30.0, "Ocio, salidas, ropa y compras personales"),
    SAVINGS("Ahorro e inversión", 20.0, "Fondo de emergencia, metas de ahorro e inversión")
}

data class FinancialPlanRecommendation(
    val monthlyIncome: Double,
    val needsBudget: Double,
    val wantsBudget: Double,
    val savingsBudget: Double,
    val categoryRecommendations: List<CategoryRecommendation>,
    val tips: List<String>
)

object BudgetRecommendationEngine {

    fun generatePlan(income: Double): FinancialPlanRecommendation {
        val safeIncome = if (income > 0.0) income else 30000.0 // Default demo income if 0

        val needs = CurrencyUtils.round(safeIncome * 0.50)
        val wants = CurrencyUtils.round(safeIncome * 0.30)
        val savings = CurrencyUtils.round(safeIncome * 0.20)

        val recs = listOf(
            CategoryRecommendation(
                categoryName = "Vivienda",
                percentageOfIncome = 20.0,
                recommendedAmount = CurrencyUtils.round(safeIncome * 0.20),
                pillar = FinancialPillar.NEEDS,
                advice = "Alquiler, hipoteca y mantenimiento no deberían superar el 20-25% de tu sueldo."
            ),
            CategoryRecommendation(
                categoryName = "Alimentación",
                percentageOfIncome = 15.0,
                recommendedAmount = CurrencyUtils.round(safeIncome * 0.15),
                pillar = FinancialPillar.NEEDS,
                advice = "Supermercado y comida básica. Comprar con lista planificada ahorra hasta un 20%."
            ),
            CategoryRecommendation(
                categoryName = "Transporte",
                percentageOfIncome = 5.0,
                recommendedAmount = CurrencyUtils.round(safeIncome * 0.05),
                pillar = FinancialPillar.NEEDS,
                advice = "Combustible, transporte público o mantenimiento."
            ),
            CategoryRecommendation(
                categoryName = "Servicios",
                percentageOfIncome = 5.0,
                recommendedAmount = CurrencyUtils.round(safeIncome * 0.05),
                pillar = FinancialPillar.NEEDS,
                advice = "Luz, agua, internet y celular. Revisa suscripciones que no uses."
            ),
            CategoryRecommendation(
                categoryName = "Salud",
                percentageOfIncome = 5.0,
                recommendedAmount = CurrencyUtils.round(safeIncome * 0.05),
                pillar = FinancialPillar.NEEDS,
                advice = "Seguro médico, consultas y medicinas."
            ),
            CategoryRecommendation(
                categoryName = "Ocio",
                percentageOfIncome = 10.0,
                recommendedAmount = CurrencyUtils.round(safeIncome * 0.10),
                pillar = FinancialPillar.WANTS,
                advice = "Salidas con amigos, cine, restaurantes y diversión."
            ),
            CategoryRecommendation(
                categoryName = "Ropa",
                percentageOfIncome = 10.0,
                recommendedAmount = CurrencyUtils.round(safeIncome * 0.10),
                pillar = FinancialPillar.WANTS,
                advice = "Prendas y calzado. Evita compras impulsivas."
            ),
            CategoryRecommendation(
                categoryName = "Otros gastos",
                percentageOfIncome = 10.0,
                recommendedAmount = CurrencyUtils.round(safeIncome * 0.10),
                pillar = FinancialPillar.WANTS,
                advice = "Margen para imprevistos menores o compras varias."
            )
        )

        val tips = mutableListOf(
            "Regla 50/30/20: Separa el 20% (RD$ ${CurrencyUtils.format(savings, "DOP")}) tan pronto cobres antes de empezar a gastar.",
            "Si tus gastos de vivienda y comida superan el 50%, reduce las compras de ocio temporalmente.",
            "Mantén un fondo de emergencia equivalente a al menos 3 meses de tus gastos fijos (RD$ ${CurrencyUtils.format(needs * 3, "DOP")})."
        )

        return FinancialPlanRecommendation(
            monthlyIncome = safeIncome,
            needsBudget = needs,
            wantsBudget = wants,
            savingsBudget = savings,
            categoryRecommendations = recs,
            tips = tips
        )
    }

    fun getSuggestedLimitForCategory(categoryName: String, monthlyIncome: Double): Double {
        val safeIncome = if (monthlyIncome > 0.0) monthlyIncome else 30000.0
        return when {
            categoryName.contains("Vivienda", ignoreCase = true) -> safeIncome * 0.20
            categoryName.contains("Alimentación", ignoreCase = true) || categoryName.contains("Comida", ignoreCase = true) -> safeIncome * 0.15
            categoryName.contains("Transporte", ignoreCase = true) -> safeIncome * 0.05
            categoryName.contains("Servicios", ignoreCase = true) -> safeIncome * 0.05
            categoryName.contains("Salud", ignoreCase = true) -> safeIncome * 0.05
            categoryName.contains("Educación", ignoreCase = true) -> safeIncome * 0.05
            categoryName.contains("Ocio", ignoreCase = true) || categoryName.contains("Entretenimiento", ignoreCase = true) -> safeIncome * 0.10
            categoryName.contains("Ropa", ignoreCase = true) -> safeIncome * 0.10
            else -> safeIncome * 0.10
        }
    }
}
