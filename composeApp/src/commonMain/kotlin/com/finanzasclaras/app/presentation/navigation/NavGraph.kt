package com.finanzasclaras.app.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.finanzasclaras.app.presentation.auth.login.LoginScreen
import com.finanzasclaras.app.presentation.auth.register.RegisterScreen
import com.finanzasclaras.app.presentation.dashboard.DashboardScreen
import com.finanzasclaras.app.presentation.onboarding.OnboardingScreen
import com.finanzasclaras.app.presentation.savings.SavingsScreen
import com.finanzasclaras.app.presentation.settings.SettingsScreen
import com.finanzasclaras.app.presentation.splash.SplashScreen
import com.finanzasclaras.app.presentation.transactions.form.AddTransactionScreen
import com.finanzasclaras.app.presentation.transactions.list.TransactionsScreen
import com.finanzasclaras.app.presentation.analysis.AnalysisScreen
import com.finanzasclaras.app.presentation.budgets.BudgetsScreen
import com.finanzasclaras.app.presentation.categories.CategoriesScreen
import com.finanzasclaras.app.presentation.investments.InvestmentsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable(NavRoutes.SPLASH) {
            SplashScreen(
                onNavigateToOnboarding = { navController.navigate(NavRoutes.ONBOARDING) { popUpTo(0) } },
                onNavigateToLogin = { navController.navigate(NavRoutes.LOGIN) { popUpTo(0) } },
                onNavigateToDashboard = { navController.navigate(NavRoutes.DASHBOARD) { popUpTo(0) } }
            )
        }

        composable(NavRoutes.ONBOARDING) {
            OnboardingScreen(
                onCompleted = { navController.navigate(NavRoutes.LOGIN) { popUpTo(0) } }
            )
        }

        composable(NavRoutes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(NavRoutes.REGISTER) },
                onNavigateToDashboard = { navController.navigate(NavRoutes.DASHBOARD) { popUpTo(0) } },
                onNavigateToLocal = { navController.navigate(NavRoutes.DASHBOARD) { popUpTo(0) } }
            )
        }

        composable(NavRoutes.REGISTER) {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onRegistered = { navController.navigate(NavRoutes.DASHBOARD) { popUpTo(0) } }
            )
        }

        composable(NavRoutes.DASHBOARD) {
            DashboardScreen(
                onNavigateToTransactions = { navController.navigate(NavRoutes.TRANSACTIONS) },
                onNavigateToAddTransaction = { navController.navigate(NavRoutes.ADD_TRANSACTION) },
                onNavigateToSavings = { navController.navigate(NavRoutes.SAVINGS) },
                onNavigateToInvestments = { navController.navigate(NavRoutes.INVESTMENTS) },
                onNavigateToAnalysis = { navController.navigate(NavRoutes.ANALYSIS) },
                onNavigateToBudgets = { navController.navigate(NavRoutes.BUDGETS) },
                onNavigateToSettings = { navController.navigate(NavRoutes.SETTINGS) }
            )
        }

        composable(NavRoutes.TRANSACTIONS) {
            TransactionsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAdd = { navController.navigate(NavRoutes.ADD_TRANSACTION) },
                onNavigateToEdit = { id -> navController.navigate(NavRoutes.editTransaction(id)) }
            )
        }

        composable(NavRoutes.ADD_TRANSACTION) {
            AddTransactionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = NavRoutes.EDIT_TRANSACTION,
            arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId") ?: ""
            AddTransactionScreen(
                transactionId = transactionId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.SAVINGS) {
            SavingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.INVESTMENTS) {
            InvestmentsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.ANALYSIS) {
            AnalysisScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToBudgets = { navController.navigate(NavRoutes.BUDGETS) }
            )
        }

        composable(NavRoutes.BUDGETS) {
            BudgetsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.CATEGORIES) {
            CategoriesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCategories = { navController.navigate(NavRoutes.CATEGORIES) },
                onLogout = {
                    navController.navigate(NavRoutes.LOGIN) { popUpTo(0) }
                }
            )
        }
    }
}
