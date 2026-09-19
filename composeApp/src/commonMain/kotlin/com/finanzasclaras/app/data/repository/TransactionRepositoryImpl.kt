package com.finanzasclaras.app.data.repository

import com.finanzasclaras.app.core.util.IdUtils
import com.finanzasclaras.app.data.local.dao.MonthlySummary
import com.finanzasclaras.app.data.local.dao.TransactionDao
import com.finanzasclaras.app.data.local.entity.TransactionEntity
import com.finanzasclaras.app.data.remote.firebase.FirebaseSyncManager
import com.finanzasclaras.app.domain.model.Transaction
import com.finanzasclaras.app.domain.model.TransactionType
import com.finanzasclaras.app.domain.repository.TransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao,
    private val syncManager: FirebaseSyncManager
) : TransactionRepository {

    private val scope = CoroutineScope(Dispatchers.Default)

    override fun getAll(): Flow<List<Transaction>> =
        transactionDao.getAll().map { entities -> entities.map { it.toDomain() } }

    override fun getByType(type: TransactionType): Flow<List<Transaction>> =
        transactionDao.getByType(TransactionType.toString(type)).map { entities -> entities.map { it.toDomain() } }

    override fun getByDateRange(startDate: Long, endDate: Long): Flow<List<Transaction>> =
        transactionDao.getByDateRange(startDate, endDate).map { entities -> entities.map { it.toDomain() } }

    override fun getByTypeAndDateRange(type: TransactionType, startDate: Long, endDate: Long): Flow<List<Transaction>> =
        transactionDao.getByTypeAndDateRange(TransactionType.toString(type), startDate, endDate)
            .map { entities -> entities.map { it.toDomain() } }

    override fun getByCategoryAndDateRange(categoryId: String, startDate: Long, endDate: Long): Flow<List<Transaction>> =
        transactionDao.getByCategoryAndDateRange(categoryId, startDate, endDate)
            .map { entities -> entities.map { it.toDomain() } }

    override suspend fun getById(id: String): Transaction? =
        transactionDao.getById(id)?.toDomain()

    override suspend fun create(transaction: Transaction) {
        transactionDao.insert(transaction.toEntity())
        scope.launch { syncManager.syncAll() }
    }

    override suspend fun update(transaction: Transaction) {
        transactionDao.insert(transaction.toEntity())
        scope.launch { syncManager.syncAll() }
    }

    override suspend fun softDelete(id: String) {
        val now = Clock.System.now().toEpochMilliseconds()
        transactionDao.softDelete(id, now)
        scope.launch { syncManager.syncAll() }
    }

    override suspend fun getTotalIncome(startDate: Long, endDate: Long): Double =
        transactionDao.getTotalIncome(startDate, endDate) ?: 0.0

    override suspend fun getTotalExpense(startDate: Long, endDate: Long): Double =
        transactionDao.getTotalExpense(startDate, endDate) ?: 0.0

    override fun getMonthlySummary(startDate: Long, endDate: Long): Flow<List<MonthlySummary>> =
        transactionDao.getMonthlySummary(startDate, endDate)

    override fun getExpenseByCategory(startDate: Long, endDate: Long): Flow<List<com.finanzasclaras.app.data.local.dao.CategoryExpense>> =
        transactionDao.getExpenseByCategory(startDate, endDate)

    private fun TransactionEntity.toDomain() = Transaction(
        id = id, categoryId = categoryId, amount = amount, currency = currency,
        amountInBase = amountInBase, type = TransactionType.fromString(type),
        note = note, date = date, createdAt = createdAt, updatedAt = updatedAt
    )

    private fun Transaction.toEntity(): TransactionEntity {
        val now = Clock.System.now().toEpochMilliseconds()
        return TransactionEntity(
            id = if (id.isEmpty()) IdUtils.randomId() else id,
            categoryId = categoryId, amount = amount, currency = currency,
            amountInBase = amountInBase, type = TransactionType.toString(type),
            note = note, date = date, createdAt = if (createdAt == 0L) now else createdAt,
            updatedAt = now, synced = false
        )
    }
}
