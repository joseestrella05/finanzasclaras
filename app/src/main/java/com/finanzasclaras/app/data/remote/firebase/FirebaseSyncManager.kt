package com.finanzasclaras.app.data.remote.firebase

import com.finanzasclaras.app.data.local.dao.InvestmentDao
import com.finanzasclaras.app.data.local.dao.SavingContributionDao
import com.finanzasclaras.app.data.local.dao.SavingGoalDao
import com.finanzasclaras.app.data.local.dao.TransactionDao
import com.finanzasclaras.app.data.local.entity.InvestmentEntity
import com.finanzasclaras.app.data.local.entity.SavingContributionEntity
import com.finanzasclaras.app.data.local.entity.SavingGoalEntity
import com.finanzasclaras.app.data.local.entity.TransactionEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseSyncManager @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val transactionDao: TransactionDao,
    private val savingGoalDao: SavingGoalDao,
    private val savingContributionDao: SavingContributionDao,
    private val investmentDao: InvestmentDao
) {
    private val userId: String?
        get() = firebaseAuth.currentUser?.uid

    suspend fun syncAll(): SyncResult {
        if (userId == null) return SyncResult.NotLoggedIn
        return try {
            syncTransactions()
            syncSavingGoals()
            syncSavingContributions()
            syncInvestments()
            SyncResult.Success
        } catch (e: Exception) {
            SyncResult.Error(e.message ?: "Sync failed")
        }
    }

    private suspend fun syncTransactions() {
        val uid = userId ?: return
        // Push local unsynced
        val unsynced = transactionDao.getUnsynced()
        for (tx in unsynced) {
            firestore.collection("users").document(uid)
                .collection("transactions").document(tx.id)
                .set(tx, SetOptions.merge())
            transactionDao.markSynced(tx.id)
        }
        // Pull remote
        val lastLocal = transactionDao.getAll()
        // Simple: fetch all remote and merge by timestamp
        val snapshot = firestore.collection("users").document(uid)
            .collection("transactions")
            .get().await()
        val remoteTransactions = snapshot.documents.mapNotNull { it.toObject<TransactionEntity>() }
        transactionDao.insertAll(remoteTransactions)
    }

    private suspend fun syncSavingGoals() {
        val uid = userId ?: return
        val unsynced = savingGoalDao.getUnsynced()
        for (goal in unsynced) {
            firestore.collection("users").document(uid)
                .collection("saving_goals").document(goal.id)
                .set(goal, SetOptions.merge())
            savingGoalDao.markSynced(goal.id)
        }
        val snapshot = firestore.collection("users").document(uid)
            .collection("saving_goals")
            .get().await()
        val remoteGoals = snapshot.documents.mapNotNull { it.toObject<SavingGoalEntity>() }
        savingGoalDao.insertAll(remoteGoals)
    }

    private suspend fun syncSavingContributions() {
        val uid = userId ?: return
        val unsynced = savingContributionDao.getUnsynced()
        for (contribution in unsynced) {
            firestore.collection("users").document(uid)
                .collection("saving_contributions").document(contribution.id)
                .set(contribution, SetOptions.merge())
            savingContributionDao.markSynced(contribution.id)
        }
    }

    private suspend fun syncInvestments() {
        val uid = userId ?: return
        val unsynced = investmentDao.getUnsynced()
        for (inv in unsynced) {
            firestore.collection("users").document(uid)
                .collection("investments").document(inv.id)
                .set(inv, SetOptions.merge())
            investmentDao.markSynced(inv.id)
        }
        val snapshot = firestore.collection("users").document(uid)
            .collection("investments")
            .get().await()
        val remoteInvestments = snapshot.documents.mapNotNull { it.toObject<InvestmentEntity>() }
        investmentDao.insertAll(remoteInvestments)
    }
}

sealed class SyncResult {
    data object Success : SyncResult()
    data object NotLoggedIn : SyncResult()
    data class Error(val message: String) : SyncResult()
}
