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
        val safeIncome = if (income > 0.0) income else 30000.0

        val needs = CurrencyUtils.round(safeIncome * 0.50)
        val wants = CurrencyUtils.round(safeIncome * 0.30)
        val savings = CurrencyUtils.round(safeIncome * 0.20)

        val recs = listOf(
            CategoryRecommendation(
                categoryName = "Vivienda",
                percentageOfIncome = 20.0,
                recommendedAmount = CurrencyUtils.round(safeIncome * 0.20),
                pillar = FinancialPillar.NEEDS,
                advice = "Alquiler, hipoteca y mantenimiento no deberían superar el 20% de tu sueldo."
            ),
            CategoryRecommendation(
                categoryName = "Supermercado",
                percentageOfIncome = 15.0,
                recommendedAmount = CurrencyUtils.round(safeIncome * 0.15),
                pillar = FinancialPillar.NEEDS,
                advice = "Supermercado y despensa básica familiar."
            ),
            CategoryRecommendation(
                categoryName = "Restaurantes",
                percentageOfIncome = 12.0,
                recommendedAmount = CurrencyUtils.round(safeIncome * 0.12),
                pillar = FinancialPillar.WANTS,
                advice = "Comidas fuera de casa, deliveries y restaurantes."
            ),
            CategoryRecommendation(
                categoryName = "Transporte",
                percentageOfIncome = 5.0,
                recommendedAmount = CurrencyUtils.round(safeIncome * 0.05),
                pillar = FinancialPillar.NEEDS,
                advice = "Combustible, transporte público y pasajes."
            ),
            CategoryRecommendation(
                categoryName = "Servicios",
                percentageOfIncome = 5.0,
                recommendedAmount = CurrencyUtils.round(safeIncome * 0.05),
                pillar = FinancialPillar.NEEDS,
                advice = "Luz, agua, internet y teléfono."
            ),
            CategoryRecommendation(
                categoryName = "Ocio",
                percentageOfIncome = 8.0,
                recommendedAmount = CurrencyUtils.round(safeIncome * 0.08),
                pillar = FinancialPillar.WANTS,
                advice = "Salidas, cine, paseos y diversión."
            ),
            CategoryRecommendation(
                categoryName = "Ropa",
                percentageOfIncome = 5.0,
                recommendedAmount = CurrencyUtils.round(safeIncome * 0.05),
                pillar = FinancialPillar.WANTS,
                advice = "Ropa y calzado planificado."
            ),
            CategoryRecommendation(
                categoryName = "Suscripciones",
                percentageOfIncome = 5.0,
                recommendedAmount = CurrencyUtils.round(safeIncome * 0.05),
                pillar = FinancialPillar.WANTS,
                advice = "Streaming y membresías mensuales."
            ),
            CategoryRecommendation(
                categoryName = "Salud",
                percentageOfIncome = 3.0,
                recommendedAmount = CurrencyUtils.round(safeIncome * 0.03),
                pillar = FinancialPillar.NEEDS,
                advice = "Farmacia, medicamentos y consultas."
            ),
            CategoryRecommendation(
                categoryName = "Educación",
                percentageOfIncome = 2.0,
                recommendedAmount = CurrencyUtils.round(safeIncome * 0.02),
                pillar = FinancialPillar.NEEDS,
                advice = "Cursos, libros y formación."
            )
        )

        val tips = mutableListOf(
            "Regla 50/30/20: Separa el 20% (RD$ ${CurrencyUtils.format(savings, "DOP")}) tan pronto cobres antes de gastar.",
            "Tus gastos presupuestados suman el 80% (RD$ ${CurrencyUtils.format(needs + wants, "DOP")}) para garantizar tu ahorro.",
            "Mantén un fondo de emergencia de 3 meses de tus gastos fijos (RD$ ${CurrencyUtils.format(needs * 3, "DOP")})."
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

    /**
     * Genera presupuestos por categoría garantizando que la suma TOTAL
     * nunca supere el 80% del sueldo disponible para gastos (dejando 20% para ahorro).
     */
    fun generateCategoryBudgets(
        categories: List<Category>,
        monthlyIncome: Double
    ): Map<String, Double> {
        val safeIncome = if (monthlyIncome > 0.0) monthlyIncome else 30000.0
        val maxExpenseBudget = safeIncome * 0.80 // 80% máximo de gastos
        val result = mutableMapOf<String, Double>()

        for (cat in categories) {
            val name = cat.name.lowercase()
            val percentage = when {
                name.contains("vivienda") || name.contains("alquiler") -> 0.20
                name.contains("supermercado") || name.contains("despensa") -> 0.15
                name.contains("comida") || name.contains("restaurante") -> 0.12
                name.contains("ocio") || name.contains("salidas") -> 0.08
                name.contains("transporte") || name.contains("gasolina") -> 0.05
                name.contains("servicios") || name.contains("luz") -> 0.05
                name.contains("ropa") || name.contains("calzado") -> 0.05
                name.contains("suscrip") || name.contains("streaming") -> 0.05
                name.contains("salud") || name.contains("farmacia") -> 0.03
                name.contains("educaci") -> 0.02
                // Categorías de deuda o agropecuarias no se presupuestan automáticamente
                else -> 0.0
            }

            if (percentage > 0.0) {
                result[cat.id] = CurrencyUtils.round(safeIncome * percentage)
            }
        }

        // Si la suma supera el 80% de gastos por alguna razón, normalizar estrictamente
        val totalAllocated = result.values.sum()
        if (totalAllocated > maxExpenseBudget && totalAllocated > 0) {
            val factor = maxExpenseBudget / totalAllocated
            val keys = result.keys.toList()
            for (k in keys) {
                result[k] = CurrencyUtils.round(result[k]!! * factor)
            }
        }

        return result
    }
}
