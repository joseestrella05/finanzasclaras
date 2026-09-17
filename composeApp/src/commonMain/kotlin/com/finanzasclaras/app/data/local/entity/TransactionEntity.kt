package com.finanzasclaras.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val categoryId: String,
    val amount: Double,
    val currency: String,
    val amountInBase: Double,
    val type: String,
    val note: String,
    val date: Long,
    val createdAt: Long,
    val updatedAt: Long,
    val synced: Boolean = false,
    val deleted: Boolean = false
)
