package com.finanzasclaras.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saving_goals")
data class SavingGoalEntity(
    @PrimaryKey val id: String,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val currency: String,
    val deadlineDate: Long?,
    val createdAt: Long,
    val updatedAt: Long,
    val completed: Boolean = false,
    val synced: Boolean = false,
    val deleted: Boolean = false
)
