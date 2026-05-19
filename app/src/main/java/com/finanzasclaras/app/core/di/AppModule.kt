package com.finanzasclaras.app.core.di

import android.content.Context
import androidx.room.Room
import com.finanzasclaras.app.core.common.Constants
import com.finanzasclaras.app.data.local.dao.CategoryBudgetDao
import com.finanzasclaras.app.data.local.dao.CategoryDao
import com.finanzasclaras.app.data.local.dao.InvestmentDao
import com.finanzasclaras.app.data.local.dao.SavingContributionDao
import com.finanzasclaras.app.data.local.dao.SavingGoalDao
import com.finanzasclaras.app.data.local.dao.TransactionDao
import com.finanzasclaras.app.data.local.database.AppDatabase
import com.finanzasclaras.app.data.remote.firebase.FirebaseSyncManager
import com.finanzasclaras.app.data.repository.CategoryRepositoryImpl
import com.finanzasclaras.app.data.repository.InvestmentRepositoryImpl
import com.finanzasclaras.app.data.repository.SavingGoalRepositoryImpl
import com.finanzasclaras.app.data.repository.TransactionRepositoryImpl
import com.finanzasclaras.app.domain.repository.AuthRepository
import com.finanzasclaras.app.domain.repository.CategoryRepository
import com.finanzasclaras.app.domain.repository.InvestmentRepository
import com.finanzasclaras.app.domain.repository.SavingGoalRepository
import com.finanzasclaras.app.domain.repository.TransactionRepository
import com.finanzasclaras.app.data.remote.firebase.FirebaseAuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            Constants.DATABASE_NAME
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides fun provideTransactionDao(db: AppDatabase): TransactionDao = db.transactionDao()
    @Provides fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()
    @Provides fun provideSavingGoalDao(db: AppDatabase): SavingGoalDao = db.savingGoalDao()
    @Provides fun provideSavingContributionDao(db: AppDatabase): SavingContributionDao = db.savingContributionDao()
    @Provides fun provideInvestmentDao(db: AppDatabase): InvestmentDao = db.investmentDao()
    @Provides fun provideCategoryBudgetDao(db: AppDatabase): CategoryBudgetDao = db.categoryBudgetDao()

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(firebaseAuth: FirebaseAuth): AuthRepository {
        return FirebaseAuthRepository(firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideTransactionRepository(
        transactionDao: TransactionDao,
        firebaseSyncManager: FirebaseSyncManager
    ): TransactionRepository {
        return TransactionRepositoryImpl(transactionDao, firebaseSyncManager)
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(categoryDao: CategoryDao): CategoryRepository {
        return CategoryRepositoryImpl(categoryDao)
    }

    @Provides
    @Singleton
    fun provideSavingGoalRepository(
        savingGoalDao: SavingGoalDao,
        savingContributionDao: SavingContributionDao,
        firebaseSyncManager: FirebaseSyncManager
    ): SavingGoalRepository {
        return SavingGoalRepositoryImpl(savingGoalDao, savingContributionDao, firebaseSyncManager)
    }

    @Provides
    @Singleton
    fun provideInvestmentRepository(
        investmentDao: InvestmentDao,
        firebaseSyncManager: FirebaseSyncManager
    ): InvestmentRepository {
        return InvestmentRepositoryImpl(investmentDao, firebaseSyncManager)
    }

    @Provides
    @Singleton
    fun provideFirebaseSyncManager(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth,
        transactionDao: TransactionDao,
        savingGoalDao: SavingGoalDao,
        savingContributionDao: SavingContributionDao,
        investmentDao: InvestmentDao
    ): FirebaseSyncManager {
        return FirebaseSyncManager(
            firestore, firebaseAuth, transactionDao,
            savingGoalDao, savingContributionDao, investmentDao
        )
    }
}
