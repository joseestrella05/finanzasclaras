package com.finanzasclaras.app.data.remote.firebase

import com.finanzasclaras.app.data.local.dao.CategoryBudgetDao
import com.finanzasclaras.app.data.local.dao.InvestmentDao
import com.finanzasclaras.app.data.local.dao.SavingContributionDao
import com.finanzasclaras.app.data.local.dao.SavingGoalDao
import com.finanzasclaras.app.data.local.dao.TransactionDao
import com.finanzasclaras.app.data.local.entity.CategoryBudgetEntity
import com.finanzasclaras.app.data.local.entity.InvestmentEntity
import com.finanzasclaras.app.data.local.entity.SavingContributionEntity
import com.finanzasclaras.app.data.local.entity.SavingGoalEntity
import com.finanzasclaras.app.data.local.entity.TransactionEntity
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore

class FirebaseSyncManager(
    private val transactionDao: TransactionDao,
    private val savingGoalDao: SavingGoalDao,
    private val savingContributionDao: SavingContributionDao,
    private val investmentDao: InvestmentDao,
    private val categoryBudgetDao: CategoryBudgetDao
) {
    private val auth by lazy { Firebase.auth }
    private val firestore by lazy { Firebase.firestore }

    val userId: String?
        get() = try { auth.currentUser?.uid } catch (_: Exception) { null }

    suspend fun syncAll(): SyncResult {
        val uid = userId
        if (uid == null) {
            println("[Firebase Sync] No user logged in, skipping sync")
            return SyncResult.NotLoggedIn
        }
        println("[Firebase Sync] Starting sync for user: $uid")
        return try {
            syncTransactions(uid)
            syncSavingGoals(uid)
            syncSavingContributions(uid)
            syncInvestments(uid)
            syncCategoryBudgets(uid)
            println("[Firebase Sync] Sync completed successfully for user: $uid")
            SyncResult.Success
        } catch (e: Throwable) {
            println("[Firebase Sync] Sync error: ${e.message}")
            SyncResult.Error(e.message ?: "Sync failed")
        }
    }

    private suspend fun syncTransactions(uid: String) {
        val unsynced = transactionDao.getUnsynced()
        println("[Firebase Sync] Found ${unsynced.size} unsynced local transactions")
        for (tx in unsynced) {
            try {
                firestore.collection("users").document(uid)
                    .collection("transactions").document(tx.id)
                    .set(tx, merge = true)
                transactionDao.markSynced(tx.id)
                println("[Firebase Sync] Uploaded transaction ${tx.id}")
            } catch (e: Throwable) {
                println("[Firebase Sync] Error uploading transaction ${tx.id}: ${e.message}")
            }
        }

        try {
            val snapshot = firestore.collection("users").document(uid)
                .collection("transactions")
                .get()
            val remoteTransactions = snapshot.documents.mapNotNull {
                try { it.data<TransactionEntity>() } catch (e: Throwable) {
                    println("[Firebase Sync] Error deserializing transaction: ${e.message}")
                    null
                }
            }
            println("[Firebase Sync] Downloaded ${remoteTransactions.size} remote transactions from Firestore")
            if (remoteTransactions.isNotEmpty()) {
                transactionDao.insertAll(remoteTransactions)
            }
        } catch (e: Throwable) {
            println("[Firebase Sync] Error downloading transactions: ${e.message}")
        }
    }

    private suspend fun syncSavingGoals(uid: String) {
        val unsynced = savingGoalDao.getUnsynced()
        for (goal in unsynced) {
            try {
                firestore.collection("users").document(uid)
                    .collection("saving_goals").document(goal.id)
                    .set(goal, merge = true)
                savingGoalDao.markSynced(goal.id)
            } catch (_: Exception) {}
        }

        try {
            val snapshot = firestore.collection("users").document(uid)
                .collection("saving_goals")
                .get()
            val remoteGoals = snapshot.documents.mapNotNull {
                try { it.data<SavingGoalEntity>() } catch (_: Exception) { null }
            }
            if (remoteGoals.isNotEmpty()) {
                savingGoalDao.insertAll(remoteGoals)
            }
        } catch (_: Exception) {}
    }

    private suspend fun syncSavingContributions(uid: String) {
        val unsynced = savingContributionDao.getUnsynced()
        for (contribution in unsynced) {
            try {
                firestore.collection("users").document(uid)
                    .collection("saving_contributions").document(contribution.id)
                    .set(contribution, merge = true)
                savingContributionDao.markSynced(contribution.id)
            } catch (_: Exception) {}
        }
    }

    private suspend fun syncInvestments(uid: String) {
        val unsynced = investmentDao.getUnsynced()
        for (inv in unsynced) {
            try {
                firestore.collection("users").document(uid)
                    .collection("investments").document(inv.id)
                    .set(inv, merge = true)
                investmentDao.markSynced(inv.id)
            } catch (_: Exception) {}
        }

        try {
            val snapshot = firestore.collection("users").document(uid)
                .collection("investments")
                .get()
            val remoteInvestments = snapshot.documents.mapNotNull {
                try { it.data<InvestmentEntity>() } catch (_: Exception) { null }
            }
            if (remoteInvestments.isNotEmpty()) {
                investmentDao.insertAll(remoteInvestments)
            }
        } catch (_: Exception) {}
    }

    private suspend fun syncCategoryBudgets(uid: String) {
        val unsynced = categoryBudgetDao.getUnsynced()
        for (budget in unsynced) {
            try {
                firestore.collection("users").document(uid)
                    .collection("category_budgets").document(budget.id)
                    .set(budget, merge = true)
                categoryBudgetDao.markSynced(budget.id)
            } catch (_: Exception) {}
        }

        try {
            val snapshot = firestore.collection("users").document(uid)
                .collection("category_budgets")
                .get()
            val remoteBudgets = snapshot.documents.mapNotNull {
                try { it.data<CategoryBudgetEntity>() } catch (_: Exception) { null }
            }
            if (remoteBudgets.isNotEmpty()) {
                categoryBudgetDao.insertAll(remoteBudgets)
            }
        } catch (_: Exception) {}
    }
}

sealed class SyncResult {
    data object Success : SyncResult()
    data object NotLoggedIn : SyncResult()
    data class Error(val message: String) : SyncResult()
}
