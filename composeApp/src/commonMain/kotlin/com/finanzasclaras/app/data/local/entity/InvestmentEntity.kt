package com.finanzasclaras.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "investments")
data class InvestmentEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,
    val amountInvested: Double,
    val currentValue: Double,
    val currency: String,
    val purchaseDate: Long,
    val notes: String,
    val createdAt: Long,
    val updatedAt: Long,
    val synced: Boolean = false,
    val deleted: Boolean = false
)
