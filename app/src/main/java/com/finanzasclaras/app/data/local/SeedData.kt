package com.finanzasclaras.app.data.local

import com.finanzasclaras.app.data.local.entity.CategoryEntity

object SeedData {
    fun getDefaultCategories(): List<CategoryEntity> = listOf(
        // Expense categories
        CategoryEntity("cat_food", "Alimentación", "Food", "restaurant", "expense", 0xFF4CAF50.toInt(), true, 0),
        CategoryEntity("cat_transport", "Transporte", "Transport", "directions_car", "expense", 0xFF2196F3.toInt(), true, 1),
        CategoryEntity("cat_health", "Salud", "Health", "local_hospital", "expense", 0xFFF44336.toInt(), true, 2),
        CategoryEntity("cat_entertainment", "Ocio", "Entertainment", "sports_esports", "expense", 0xFFFF9800.toInt(), true, 3),
        CategoryEntity("cat_housing", "Vivienda", "Housing", "home", "expense", 0xFF9C27B0.toInt(), true, 4),
        CategoryEntity("cat_education", "Educación", "Education", "school", "expense", 0xFF00BCD4.toInt(), true, 5),
        CategoryEntity("cat_utilities", "Servicios", "Utilities", "bolt", "expense", 0xFF607D8B.toInt(), true, 6),
        CategoryEntity("cat_clothing", "Ropa", "Clothing", "checkroom", "expense", 0xFFE91E63.toInt(), true, 7),
        CategoryEntity("cat_credit_card", "Tarjeta de Crédito", "Credit Card", "credit_card", "expense", 0xFF673AB7.toInt(), true, 8),
        CategoryEntity("cat_agriculture", "Ganadería/Agro", "Agriculture", "agriculture", "expense", 0xFF795548.toInt(), true, 9),
        CategoryEntity("cat_other_expense", "Otros gastos", "Other expenses", "more_horiz", "expense", 0xFF9E9E9E.toInt(), true, 10),
        // Income categories
        CategoryEntity("cat_salary", "Salario", "Salary", "work", "income", 0xFF4CAF50.toInt(), true, 9),
        CategoryEntity("cat_investment", "Inversión", "Investment", "trending_up", "income", 0xFF2196F3.toInt(), true, 10),
        CategoryEntity("cat_freelance", "Freelance", "Freelance", "computer", "income", 0xFFFF9800.toInt(), true, 11),
        CategoryEntity("cat_gift", "Regalo", "Gift", "card_giftcard", "income", 0xFFE91E63.toInt(), true, 14),
        CategoryEntity("cat_agri_income", "Ventas Ganadería", "Agri Sales", "agriculture", "income", 0xFF795548.toInt(), true, 15),
        CategoryEntity("cat_other_income", "Otros ingresos", "Other income", "attach_money", "income", 0xFF9E9E9E.toInt(), true, 16)
    )
}
