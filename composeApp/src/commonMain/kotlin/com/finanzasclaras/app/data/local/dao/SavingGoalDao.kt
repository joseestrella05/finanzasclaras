package com.finanzasclaras.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.finanzasclaras.app.data.local.entity.SavingGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingGoalDao {
    @Query("SELECT * FROM saving_goals WHERE deleted = 0 ORDER BY completed ASC, deadlineDate ASC")
    fun getAll(): Flow<List<SavingGoalEntity>>

    @Query("SELECT * FROM saving_goals WHERE id = :id")
    suspend fun getById(id: String): SavingGoalEntity?

    @Query("SELECT SUM(currentAmount) FROM saving_goals WHERE deleted = 0")
    suspend fun getTotalSaved(): Double?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: SavingGoalEntity)

    @Query("UPDATE saving_goals SET currentAmount = currentAmount + :amount, updatedAt = :now WHERE id = :goalId")
    suspend fun addContribution(goalId: String, amount: Double, now: Long)

    @Query("UPDATE saving_goals SET completed = 1, updatedAt = :now WHERE id = :goalId")
    suspend fun markCompleted(goalId: String, now: Long)

    @Query("UPDATE saving_goals SET deleted = 1, updatedAt = :now WHERE id = :id")
    suspend fun softDelete(id: String, now: Long)

    @Query("SELECT * FROM saving_goals WHERE synced = 0 AND deleted = 0")
    suspend fun getUnsynced(): List<SavingGoalEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(goals: List<SavingGoalEntity>)

    @Query("UPDATE saving_goals SET synced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)
}
