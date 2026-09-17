package com.finanzasclaras.app.domain.model

data class SavingGoal(
    val id: String,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val currency: String,
    val deadlineDate: Long?,
    val createdAt: Long,
    val updatedAt: Long,
    val completed: Boolean
) {
    val progress: Float
        get() = if (targetAmount > 0) (currentAmount / targetAmount).toFloat().coerceIn(0f, 1f) else 0f

    val remaining: Double
        get() = (targetAmount - currentAmount).coerceAtLeast(0.0)
}

data class SavingContribution(
    val id: String,
    val goalId: String,
    val amount: Double,
    val currency: String,
    val date: Long,
    val note: String
)
