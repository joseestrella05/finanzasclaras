package com.finanzasclaras.app.data.repository

import com.finanzasclaras.app.data.local.dao.CategoryBudgetDao
import com.finanzasclaras.app.data.local.entity.CategoryBudgetEntity
import com.finanzasclaras.app.data.remote.firebase.FirebaseSyncManager
import com.finanzasclaras.app.domain.model.CategoryBudget
import com.finanzasclaras.app.domain.repository.BudgetRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class BudgetRepositoryImpl(
    private val categoryBudgetDao: CategoryBudgetDao,
    private val syncManager: FirebaseSyncManager
) : BudgetRepository {

    private val scope = CoroutineScope(Dispatchers.Default)

    override fun getBudgetsForMonth(month: Int, year: Int): Flow<List<CategoryBudget>> =
        categoryBudgetDao.getByMonth(month, year).map { list -> list.map { it.toDomain() } }

    override suspend fun getBudgetForCategory(categoryId: String, month: Int, year: Int): CategoryBudget? =
        categoryBudgetDao.getByCategoryAndMonth(categoryId, month, year)?.toDomain()

    override suspend fun saveBudget(budget: CategoryBudget) {
        categoryBudgetDao.insert(budget.toEntity(synced = false))
        scope.launch {
            try { syncManager.syncAll() } catch (_: Throwable) {}
        }
    }

    override suspend fun saveBudgets(budgets: List<CategoryBudget>) {
        categoryBudgetDao.insertAll(budgets.map { it.toEntity(synced = false) })
        scope.launch {
            try { syncManager.syncAll() } catch (_: Throwable) {}
        }
    }

    override suspend fun deleteBudget(id: String) {
        categoryBudgetDao.delete(id)
        try {
            syncManager.deleteCategoryBudgetRemote(id)
        } catch (_: Throwable) {}
    }

    override suspend fun clearMonthBudgets(month: Int, year: Int) {
        val current = categoryBudgetDao.getAllList().filter { it.month == month && it.year == year }
        categoryBudgetDao.deleteMonth(month, year)
        for (b in current) {
            try {
                syncManager.deleteCategoryBudgetRemote(b.id)
            } catch (_: Throwable) {}
        }
    }

    private fun CategoryBudgetEntity.toDomain() = CategoryBudget(
        id = id,
        categoryId = categoryId,
        monthlyLimit = monthlyLimit,
        month = month,
        year = year,
        synced = synced
    )

    private fun CategoryBudget.toEntity(synced: Boolean = this.synced) = CategoryBudgetEntity(
        id = id,
        categoryId = categoryId,
        monthlyLimit = monthlyLimit,
        month = month,
        year = year,
        synced = synced
    )
}
