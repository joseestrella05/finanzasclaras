package com.finanzasclaras.app.domain.model

data class Investment(
    val id: String,
    val name: String,
    val type: String,
    val amountInvested: Double,
    val currentValue: Double,
    val currency: String,
    val purchaseDate: Long,
    val notes: String,
    val createdAt: Long,
    val updatedAt: Long
) {
    val profitLoss: Double
        get() = currentValue - amountInvested

    val profitLossPercentage: Double
        get() = if (amountInvested > 0) (profitLoss / amountInvested) * 100 else 0.0

    val isProfitable: Boolean
        get() = profitLoss >= 0
}
