package com.finanzasclaras.app.data.repository

import com.finanzasclaras.app.data.local.dao.SavingContributionDao
import com.finanzasclaras.app.data.local.dao.SavingGoalDao
import com.finanzasclaras.app.data.local.entity.SavingContributionEntity
import com.finanzasclaras.app.data.local.entity.SavingGoalEntity
import com.finanzasclaras.app.data.remote.firebase.FirebaseSyncManager
import com.finanzasclaras.app.domain.model.SavingContribution
import com.finanzasclaras.app.domain.model.SavingGoal
import com.finanzasclaras.app.domain.repository.SavingGoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class SavingGoalRepositoryImpl @Inject constructor(
    private val savingGoalDao: SavingGoalDao,
    private val savingContributionDao: SavingContributionDao,
    private val syncManager: FirebaseSyncManager
) : SavingGoalRepository {

    override fun getAll(): Flow<List<SavingGoal>> =
        savingGoalDao.getAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getById(id: String): SavingGoal? =
        savingGoalDao.getById(id)?.toDomain()

    override suspend fun create(goal: SavingGoal) {
        savingGoalDao.insert(goal.toEntity())
    }

    override suspend fun addContribution(goalId: String, contribution: SavingContribution) {
        val now = System.currentTimeMillis()
        savingContributionDao.insert(contribution.toEntity())
        savingGoalDao.addContribution(goalId, contribution.amount, now)

        val goal = savingGoalDao.getById(goalId) ?: return
        if (goal.currentAmount >= goal.targetAmount) {
            savingGoalDao.markCompleted(goalId, now)
        }
    }

    override suspend fun delete(id: String) {
        savingGoalDao.softDelete(id, System.currentTimeMillis())
    }

    override suspend fun getTotalSaved(): Double =
        savingGoalDao.getTotalSaved() ?: 0.0

    private fun SavingGoalEntity.toDomain() = SavingGoal(
        id = id, name = name, targetAmount = targetAmount,
        currentAmount = currentAmount, currency = currency,
        deadlineDate = deadlineDate, createdAt = createdAt,
        updatedAt = updatedAt, completed = completed
    )

    private fun SavingGoal.toEntity() = SavingGoalEntity(
        id = id.ifEmpty { UUID.randomUUID().toString() },
        name = name, targetAmount = targetAmount,
        currentAmount = currentAmount, currency = currency,
        deadlineDate = deadlineDate, createdAt = if (createdAt == 0L) System.currentTimeMillis() else createdAt,
        updatedAt = System.currentTimeMillis(), completed = completed
    )

    private fun SavingContributionEntity.toDomain() = SavingContribution(
        id = id, goalId = goalId, amount = amount, currency = currency,
        date = date, note = note
    )

    private fun SavingContribution.toEntity() = SavingContributionEntity(
        id = id.ifEmpty { UUID.randomUUID().toString() },
        goalId = goalId, amount = amount, currency = currency,
        date = date, note = note, createdAt = System.currentTimeMillis()
    )
}
