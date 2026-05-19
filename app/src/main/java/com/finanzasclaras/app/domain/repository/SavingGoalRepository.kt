package com.finanzasclaras.app.domain.repository

import com.finanzasclaras.app.domain.model.SavingContribution
import com.finanzasclaras.app.domain.model.SavingGoal
import kotlinx.coroutines.flow.Flow

interface SavingGoalRepository {
    fun getAll(): Flow<List<SavingGoal>>
    suspend fun getById(id: String): SavingGoal?
    suspend fun create(goal: SavingGoal)
    suspend fun addContribution(goalId: String, contribution: SavingContribution)
    suspend fun delete(id: String)
    suspend fun getTotalSaved(): Double
}
