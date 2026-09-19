package com.finanzasclaras.app.core.di

import com.finanzasclaras.app.core.local.UserPreferences
import com.finanzasclaras.app.data.local.database.AppDatabase
import com.finanzasclaras.app.data.local.database.createRoomDatabase
import com.finanzasclaras.app.data.remote.firebase.FirebaseAuthRepository
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
import com.finanzasclaras.app.presentation.analysis.AnalysisViewModel
import com.finanzasclaras.app.presentation.auth.login.LoginViewModel
import com.finanzasclaras.app.presentation.auth.register.RegisterViewModel
import com.finanzasclaras.app.presentation.categories.CategoriesViewModel
import com.finanzasclaras.app.presentation.dashboard.DashboardViewModel
import com.finanzasclaras.app.presentation.investments.InvestmentsViewModel
import com.finanzasclaras.app.presentation.onboarding.OnboardingViewModel
import com.finanzasclaras.app.presentation.savings.SavingsViewModel
import com.finanzasclaras.app.presentation.settings.SettingsViewModel
import com.finanzasclaras.app.presentation.splash.SplashViewModel
import com.finanzasclaras.app.presentation.transactions.form.AddTransactionViewModel
import com.finanzasclaras.app.presentation.transactions.list.TransactionsViewModel
import com.finanzasclaras.app.domain.repository.BudgetRepository
import com.finanzasclaras.app.data.repository.BudgetRepositoryImpl
import com.finanzasclaras.app.presentation.budgets.BudgetsViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val appModule = module {
    // Database & DAOs
    single<AppDatabase> { createRoomDatabase() }
    single { get<AppDatabase>().transactionDao() }
    single { get<AppDatabase>().categoryDao() }
    single { get<AppDatabase>().savingGoalDao() }
    single { get<AppDatabase>().savingContributionDao() }
    single { get<AppDatabase>().investmentDao() }
    single { get<AppDatabase>().categoryBudgetDao() }

    // User Preferences
    single { UserPreferences() }

    // Firebase Auth & Sync
    single<AuthRepository> { FirebaseAuthRepository() }
    single { FirebaseSyncManager(get(), get(), get(), get(), get(), get()) }

    // Repositories
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }
    single<TransactionRepository> { TransactionRepositoryImpl(get(), get()) }
    single<SavingGoalRepository> { SavingGoalRepositoryImpl(get(), get(), get()) }
    single<InvestmentRepository> { InvestmentRepositoryImpl(get(), get()) }
    single<BudgetRepository> { BudgetRepositoryImpl(get(), get()) }

    // ViewModels
    viewModelOf(::SplashViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::DashboardViewModel)
    viewModelOf(::TransactionsViewModel)
    viewModelOf(::AddTransactionViewModel)
    viewModelOf(::SavingsViewModel)
    viewModelOf(::InvestmentsViewModel)
    viewModelOf(::CategoriesViewModel)
    viewModelOf(::AnalysisViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::BudgetsViewModel)
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(appModule)
    }
