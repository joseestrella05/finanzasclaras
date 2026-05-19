package com.finanzasclaras.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_budgets")
data class CategoryBudgetEntity(
    @PrimaryKey val id: String,
    val categoryId: String,
    val monthlyLimit: Double,
    val month: Int,
    val year: Int,
    val synced: Boolean = false
)
