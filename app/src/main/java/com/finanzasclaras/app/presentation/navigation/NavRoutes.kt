package com.finanzasclaras.app.presentation.navigation

object NavRoutes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
    const val TRANSACTIONS = "transactions"
    const val ADD_TRANSACTION = "add_transaction"
    const val EDIT_TRANSACTION = "edit_transaction/{transactionId}"
    const val SAVINGS = "savings"
    const val ADD_SAVING_GOAL = "add_saving_goal"
    const val SAVING_GOAL_DETAIL = "saving_goal_detail/{goalId}"
    const val INVESTMENTS = "investments"
    const val ADD_INVESTMENT = "add_investment"
    const val ANALYSIS = "analysis"
    const val CATEGORIES = "categories"
    const val SETTINGS = "settings"

    fun editTransaction(id: String) = "edit_transaction/$id"
    fun savingGoalDetail(id: String) = "saving_goal_detail/$id"
}
