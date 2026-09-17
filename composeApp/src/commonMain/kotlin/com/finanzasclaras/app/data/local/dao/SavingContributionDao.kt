package com.finanzasclaras.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.finanzasclaras.app.data.local.entity.SavingContributionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingContributionDao {
    @Query("SELECT * FROM saving_contributions WHERE goalId = :goalId ORDER BY date DESC")
    fun getByGoal(goalId: String): Flow<List<SavingContributionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contribution: SavingContributionEntity)

    @Query("SELECT * FROM saving_contributions WHERE synced = 0")
    suspend fun getUnsynced(): List<SavingContributionEntity>

    @Query("UPDATE saving_contributions SET synced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(contributions: List<SavingContributionEntity>)
}
