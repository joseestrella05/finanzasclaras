package com.finanzasclaras.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.finanzasclaras.app.data.local.dao.CategoryBudgetDao
import com.finanzasclaras.app.data.local.dao.CategoryDao
import com.finanzasclaras.app.data.local.dao.InvestmentDao
import com.finanzasclaras.app.data.local.dao.SavingContributionDao
import com.finanzasclaras.app.data.local.dao.SavingGoalDao
import com.finanzasclaras.app.data.local.dao.TransactionDao
import com.finanzasclaras.app.data.local.entity.CategoryBudgetEntity
import com.finanzasclaras.app.data.local.entity.CategoryEntity
import com.finanzasclaras.app.data.local.entity.InvestmentEntity
import com.finanzasclaras.app.data.local.entity.SavingContributionEntity
import com.finanzasclaras.app.data.local.entity.SavingGoalEntity
import com.finanzasclaras.app.data.local.entity.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        SavingGoalEntity::class,
        SavingContributionEntity::class,
        InvestmentEntity::class,
        CategoryBudgetEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun savingGoalDao(): SavingGoalDao
    abstract fun savingContributionDao(): SavingContributionDao
    abstract fun investmentDao(): InvestmentDao
    abstract fun categoryBudgetDao(): CategoryBudgetDao
}
