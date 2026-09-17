package com.finanzasclaras.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.finanzasclaras.app.data.local.entity.InvestmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InvestmentDao {
    @Query("SELECT * FROM investments WHERE deleted = 0 ORDER BY purchaseDate DESC")
    fun getAll(): Flow<List<InvestmentEntity>>

    @Query("SELECT * FROM investments WHERE id = :id")
    suspend fun getById(id: String): InvestmentEntity?

    @Query("SELECT SUM(amountInvested) FROM investments WHERE deleted = 0")
    suspend fun getTotalInvested(): Double?

    @Query("SELECT SUM(currentValue) FROM investments WHERE deleted = 0")
    suspend fun getTotalCurrentValue(): Double?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(investment: InvestmentEntity)

    @androidx.room.Update
    suspend fun update(investment: InvestmentEntity)

    @Query("UPDATE investments SET deleted = 1, updatedAt = :now WHERE id = :id")
    suspend fun softDelete(id: String, now: Long)

    @Query("SELECT * FROM investments WHERE synced = 0 AND deleted = 0")
    suspend fun getUnsynced(): List<InvestmentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(investments: List<InvestmentEntity>)

    @Query("UPDATE investments SET synced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)
}
