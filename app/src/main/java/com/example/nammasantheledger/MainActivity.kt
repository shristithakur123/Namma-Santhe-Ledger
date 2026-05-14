package com.example.nammasantheledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nammasantheledger.data.LedgerViewModel
import com.example.nammasantheledger.ui.theme.NammaSantheLedgerTheme
import com.example.nammasantheledger.uiscreen.screens.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NammaSantheLedgerTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(), // ✅ fixes status bar overlap
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val viewModel: LedgerViewModel = viewModel()

                    NavHost(
                        navController    = navController,
                        startDestination = "splash"
                    ) {
                        composable("splash") {
                            SplashScreen(navController)
                        }
                        composable("login") {
                            LoginScreen(navController, viewModel)
                        }
                        composable("register") {
                            CreateAccountScreen(navController, viewModel)
                        }
                        composable("dashboard") {
                            DashboardScreen(navController, viewModel)
                        }
                        composable("quickEntry") {
                            QuickEntryScreen(navController, viewModel)
                        }
                        composable("customers") {
                            CustomerListScreen(navController, viewModel)
                        }
                        composable("customerDetail/{name}") { backStackEntry ->
                            val name = backStackEntry.arguments?.getString("name") ?: ""
                            CustomerDetailScreen(navController, viewModel, name)
                        }
                        composable("summary") {
                            DailySummaryScreen(navController, viewModel)
                        }
                        composable("reminders") {
                            WhatsAppReminderScreen(navController, viewModel)
                        }
                        composable("reminders/{name}") { backStackEntry ->
                            val name = backStackEntry.arguments?.getString("name") ?: ""
                            WhatsAppReminderScreenWithCustomer(navController, viewModel, name)
                        }
                        composable("transactions") {
                            TransactionListScreen(navController, viewModel)
                        }
                    }
                }
            }
        }
    }
}