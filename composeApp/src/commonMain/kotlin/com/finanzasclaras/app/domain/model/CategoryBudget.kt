package com.finanzasclaras.app.domain.model

data class CategoryBudget(
    val id: String,
    val categoryId: String,
    val monthlyLimit: Double,
    val month: Int,
    val year: Int,
    val synced: Boolean = false
)
