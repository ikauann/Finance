package com.personal.financeapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.personal.financeapp.presentation.navigation.AppNavGraph
import com.personal.financeapp.presentation.navigation.Screen
import com.personal.financeapp.ui.theme.FinanceAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private var navController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinanceAppTheme {
                val navController = rememberNavController()
                this.navController = navController
                AppNavGraph(navController = navController)
            }
            
            LaunchedEffect(intent) {
                handlePluggyRedirect(intent)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handlePluggyRedirect(intent)
    }

    private fun handlePluggyRedirect(intent: Intent?) {
        val data = intent?.data
        if (data != null && data.scheme == "financeapp" && data.host == "callback") {
            val itemId = data.getQueryParameter("itemId")
            navController?.navigate(Screen.ConnectBank.createRoute(itemId)) {
                popUpTo(Screen.ConnectBank.route) { inclusive = true }
            }
        }
    }
}
