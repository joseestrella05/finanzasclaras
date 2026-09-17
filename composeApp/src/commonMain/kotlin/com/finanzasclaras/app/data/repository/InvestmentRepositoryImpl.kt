package com.finanzasclaras.app.data.repository

import com.finanzasclaras.app.core.util.IdUtils
import com.finanzasclaras.app.data.local.dao.InvestmentDao
import com.finanzasclaras.app.data.local.entity.InvestmentEntity
import com.finanzasclaras.app.data.remote.firebase.FirebaseSyncManager
import com.finanzasclaras.app.domain.model.Investment
import com.finanzasclaras.app.domain.repository.InvestmentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class InvestmentRepositoryImpl(
    private val investmentDao: InvestmentDao,
    private val syncManager: FirebaseSyncManager
) : InvestmentRepository {

    private val scope = CoroutineScope(Dispatchers.Default)

    override fun getAll(): Flow<List<Investment>> =
        investmentDao.getAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getById(id: String): Investment? =
        investmentDao.getById(id)?.toDomain()

    override suspend fun create(investment: Investment) {
        investmentDao.insert(investment.toEntity())
        scope.launch { syncManager.syncAll() }
    }

    override suspend fun update(investment: Investment) {
        investmentDao.update(investment.toEntity())
        scope.launch { syncManager.syncAll() }
    }

    override suspend fun delete(id: String) {
        val now = Clock.System.now().toEpochMilliseconds()
        investmentDao.softDelete(id, now)
        scope.launch { syncManager.syncAll() }
    }

    override suspend fun getTotalInvested(): Double =
        investmentDao.getTotalInvested() ?: 0.0

    override suspend fun getTotalCurrentValue(): Double =
        investmentDao.getTotalCurrentValue() ?: 0.0

    private fun InvestmentEntity.toDomain() = Investment(
        id = id, name = name, type = type,
        amountInvested = amountInvested, currentValue = currentValue,
        currency = currency, purchaseDate = purchaseDate,
        notes = notes, createdAt = createdAt, updatedAt = updatedAt
    )

    private fun Investment.toEntity(): InvestmentEntity {
        val now = Clock.System.now().toEpochMilliseconds()
        return InvestmentEntity(
            id = if (id.isEmpty()) IdUtils.randomId() else id,
            name = name, type = type,
            amountInvested = amountInvested, currentValue = currentValue,
            currency = currency, purchaseDate = purchaseDate,
            notes = notes, createdAt = if (createdAt == 0L) now else createdAt,
            updatedAt = now, synced = false
        )
    }
}
