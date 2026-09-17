package com.finanzasclaras.app.data.repository

import com.finanzasclaras.app.core.util.IdUtils
import com.finanzasclaras.app.data.local.dao.SavingContributionDao
import com.finanzasclaras.app.data.local.dao.SavingGoalDao
import com.finanzasclaras.app.data.local.entity.SavingContributionEntity
import com.finanzasclaras.app.data.local.entity.SavingGoalEntity
import com.finanzasclaras.app.data.remote.firebase.FirebaseSyncManager
import com.finanzasclaras.app.domain.model.SavingContribution
import com.finanzasclaras.app.domain.model.SavingGoal
import com.finanzasclaras.app.domain.repository.SavingGoalRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class SavingGoalRepositoryImpl(
    private val savingGoalDao: SavingGoalDao,
    private val savingContributionDao: SavingContributionDao,
    private val syncManager: FirebaseSyncManager
) : SavingGoalRepository {

    private val scope = CoroutineScope(Dispatchers.Default)

    override fun getAll(): Flow<List<SavingGoal>> =
        savingGoalDao.getAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getById(id: String): SavingGoal? =
        savingGoalDao.getById(id)?.toDomain()

    override suspend fun create(goal: SavingGoal) {
        savingGoalDao.insert(goal.toEntity())
        scope.launch { syncManager.syncAll() }
    }

    override suspend fun addContribution(goalId: String, contribution: SavingContribution) {
        val now = Clock.System.now().toEpochMilliseconds()
        savingContributionDao.insert(contribution.toEntity())
        savingGoalDao.addContribution(goalId, contribution.amount, now)

        val goal = savingGoalDao.getById(goalId) ?: return
        if (goal.currentAmount >= goal.targetAmount) {
            savingGoalDao.markCompleted(goalId, now)
        }
        scope.launch { syncManager.syncAll() }
    }

    override suspend fun delete(id: String) {
        val now = Clock.System.now().toEpochMilliseconds()
        savingGoalDao.softDelete(id, now)
        scope.launch { syncManager.syncAll() }
    }

    override suspend fun getTotalSaved(): Double =
        savingGoalDao.getTotalSaved() ?: 0.0

    private fun SavingGoalEntity.toDomain() = SavingGoal(
        id = id, name = name, targetAmount = targetAmount,
        currentAmount = currentAmount, currency = currency,
        deadlineDate = deadlineDate, createdAt = createdAt,
        updatedAt = updatedAt, completed = completed
    )

    private fun SavingGoal.toEntity(): SavingGoalEntity {
        val now = Clock.System.now().toEpochMilliseconds()
        return SavingGoalEntity(
            id = if (id.isEmpty()) IdUtils.randomId() else id,
            name = name, targetAmount = targetAmount,
            currentAmount = currentAmount, currency = currency,
            deadlineDate = deadlineDate, createdAt = if (createdAt == 0L) now else createdAt,
            updatedAt = now, completed = completed, synced = false
        )
    }

    private fun SavingContributionEntity.toDomain() = SavingContribution(
        id = id, goalId = goalId, amount = amount, currency = currency,
        date = date, note = note
    )

    private fun SavingContribution.toEntity(): SavingContributionEntity {
        val now = Clock.System.now().toEpochMilliseconds()
        return SavingContributionEntity(
            id = if (id.isEmpty()) IdUtils.randomId() else id,
            goalId = goalId, amount = amount, currency = currency,
            date = date, note = note, createdAt = now, synced = false
        )
    }
}
