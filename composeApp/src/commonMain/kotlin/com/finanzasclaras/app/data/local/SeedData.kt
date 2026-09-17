package com.finanzasclaras.app.data.local

import com.finanzasclaras.app.data.local.entity.CategoryEntity

object SeedData {
    fun getDefaultCategories(): List<CategoryEntity> = listOf(
        // Expense categories
        CategoryEntity("cat_credit_card", "Pago de Tarjeta de Crédito", "Credit Card Payment", "credit_card", "expense", 0xFF6366F1.toInt(), true, 0),
        CategoryEntity("cat_loan_payment", "Pago de Préstamo / Cuota", "Loan Payment", "payments", "expense", 0xFFF59E0B.toInt(), true, 1),
        CategoryEntity("cat_supermarket", "Supermercado y Despensa", "Supermarket", "shopping_cart", "expense", 0xFF10B981.toInt(), true, 2),
        CategoryEntity("cat_utilities", "Servicios (Luz, Agua, Net)", "Utilities", "bolt", "expense", 0xFF0EA5E9.toInt(), true, 3),
        CategoryEntity("cat_subscriptions", "Suscripciones (Streaming)", "Subscriptions", "computer", "expense", 0xFF8B5CF6.toInt(), true, 4),
        CategoryEntity("cat_food", "Comida y Restaurantes", "Food & Dining", "restaurant", "expense", 0xFFF97316.toInt(), true, 5),
        CategoryEntity("cat_transport", "Transporte y Gasolina", "Transport", "directions_car", "expense", 0xFF3B82F6.toInt(), true, 6),
        CategoryEntity("cat_housing", "Vivienda y Alquiler", "Housing", "home", "expense", 0xFF64748B.toInt(), true, 7),
        CategoryEntity("cat_health", "Salud y Farmacia", "Health & Medical", "local_hospital", "expense", 0xFFEF4444.toInt(), true, 8),
        CategoryEntity("cat_education", "Educación", "Education", "school", "expense", 0xFF14B8A6.toInt(), true, 9),
        CategoryEntity("cat_clothing", "Ropa y Calzado", "Clothing", "checkroom", "expense", 0xFFEC4899.toInt(), true, 10),
        CategoryEntity("cat_entertainment", "Ocio y Salidas", "Entertainment", "sports_esports", "expense", 0xFFA855F7.toInt(), true, 11),
        CategoryEntity("cat_agriculture", "Ganadería / Agro", "Agriculture", "agriculture", "expense", 0xFF795548.toInt(), true, 12),
        CategoryEntity("cat_other_expense", "Otros Gastos", "Other Expenses", "more_horiz", "expense", 0xFF94A3B8.toInt(), true, 13),

        // Income categories
        CategoryEntity("cat_salary", "Salario / Sueldo", "Salary", "work", "income", 0xFF10B981.toInt(), true, 0),
        CategoryEntity("cat_loan_income", "Préstamo Recibido", "Loan Received", "payments", "income", 0xFF3B82F6.toInt(), true, 1),
        CategoryEntity("cat_investment", "Rendimientos de Inversión", "Investment Returns", "trending_up", "income", 0xFF06B6D4.toInt(), true, 2),
        CategoryEntity("cat_freelance", "Freelance / Negocios", "Freelance", "computer", "income", 0xFFF59E0B.toInt(), true, 3),
        CategoryEntity("cat_gift", "Regalos y Bonos", "Gifts & Bonuses", "card_giftcard", "income", 0xFF8B5CF6.toInt(), true, 4),
        CategoryEntity("cat_agri_income", "Ventas Agropecuarias", "Agri Sales", "agriculture", "income", 0xFF795548.toInt(), true, 5),
        CategoryEntity("cat_other_income", "Otros Ingresos", "Other Income", "attach_money", "income", 0xFF64748B.toInt(), true, 6)
    )
}
