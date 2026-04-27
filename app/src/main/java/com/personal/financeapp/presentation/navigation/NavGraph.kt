package com.personal.financeapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.personal.financeapp.presentation.screens.dashboard.DashboardScreen
import com.personal.financeapp.presentation.screens.transactions.TransactionsScreen
import com.personal.financeapp.presentation.screens.receipt.ReceiptScanScreen
import com.personal.financeapp.presentation.screens.assistant.AssistantScreen
import com.personal.financeapp.presentation.screens.goals.GoalsScreen
import com.personal.financeapp.presentation.screens.connect.ConnectBankScreen

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Transactions : Screen("transactions")
    object Receipt : Screen("receipt")
    object Assistant : Screen("assistant")
    object Goals : Screen("goals")
    object ConnectBank : Screen("connect_bank?itemId={itemId}") {
        fun createRoute(itemId: String?) = if (itemId != null) "connect_bank?itemId=$itemId" else "connect_bank"
    }
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController)
        }
        composable(Screen.Transactions.route) {
            TransactionsScreen(navController)
        }
        composable(Screen.Receipt.route) {
            ReceiptScanScreen(navController)
        }
        composable(Screen.Assistant.route) {
            AssistantScreen(navController)
        }
        composable(Screen.Goals.route) {
            GoalsScreen(navController)
        }
        composable(
            route = Screen.ConnectBank.route,
            arguments = listOf(navArgument("itemId") { 
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")
            ConnectBankScreen(navController, itemId = itemId)
        }
    }
}
