package com.finanzasclaras.app.domain.model

data class Transaction(
    val id: String,
    val categoryId: String,
    val amount: Double,
    val currency: String,
    val amountInBase: Double,
    val type: TransactionType,
    val note: String,
    val date: Long,
    val createdAt: Long,
    val updatedAt: Long
)

enum class TransactionType {
    INCOME, EXPENSE;

    companion object {
        fun fromString(value: String): TransactionType =
            when (value.lowercase()) {
                "income" -> INCOME
                else -> EXPENSE
            }

        fun toString(type: TransactionType): String =
            when (type) {
                INCOME -> "income"
                EXPENSE -> "expense"
            }
    }
}
