package com.finanzasclaras.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "saving_contributions")
data class SavingContributionEntity(
    @PrimaryKey val id: String,
    val goalId: String,
    val amount: Double,
    val currency: String,
    val date: Long,
    val note: String,
    val createdAt: Long,
    val synced: Boolean = false
)
