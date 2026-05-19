package com.finanzasclaras.app.domain.repository

import com.finanzasclaras.app.domain.model.Investment
import kotlinx.coroutines.flow.Flow

interface InvestmentRepository {
    fun getAll(): Flow<List<Investment>>
    suspend fun getById(id: String): Investment?
    suspend fun create(investment: Investment)
    suspend fun update(investment: Investment)
    suspend fun delete(id: String)
    suspend fun getTotalInvested(): Double
    suspend fun getTotalCurrentValue(): Double
}
