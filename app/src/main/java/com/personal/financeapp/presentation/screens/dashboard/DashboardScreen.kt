package com.personal.financeapp.presentation.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.personal.financeapp.presentation.navigation.Screen
import com.personal.financeapp.presentation.components.PieChartComponent
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showAddAlertDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddTransactionDialog(
            categories = uiState.categories,
            onDismiss = { showAddDialog = false },
            onConfirm = { transaction ->
                viewModel.onEvent(DashboardEvent.AddTransaction(transaction))
                showAddDialog = false
            }
        )
    }

    if (showAddAlertDialog) {
        AddAlertDialog(
            onDismiss = { showAddAlertDialog = false },
            onConfirm = { alert ->
                viewModel.onEvent(DashboardEvent.AddAlert(alert))
                showAddAlertDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Finance App", style = MaterialTheme.typography.titleLarge) },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.ConnectBank.route) }) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = "Conectar Banco",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        bottomBar = {
            com.personal.financeapp.presentation.components.BottomNavigationBar(navController)
        },
        floatingActionButton = {
            Box {
                var expanded by remember { mutableStateOf(false) }
                FloatingActionButton(
                    onClick = { expanded = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    elevation = FloatingActionButtonDefaults.elevation(6.dp)
                ) {
                    Icon(Icons.Default.Add, "Adicionar")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Nova Transação") },
                        onClick = { expanded = false; showAddDialog = true }
                    )
                    DropdownMenuItem(
                        text = { Text("Novo Alerta (Veículo)") },
                        onClick = { expanded = false; showAddAlertDialog = true }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "💰 Resumo Financeiro",
                style = MaterialTheme.typography.headlineMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val currencyFormatter = remember { 
                NumberFormat.getCurrencyInstance(Locale("pt", "BR")) 
            }

            SummaryCard(
                title = "💵 Saldo do Mês",
                amount = uiState.totalBalance,
                variation = uiState.balanceVariationString,
                currencyFormatter = currencyFormatter
            )

            Spacer(modifier = Modifier.height(16.dp))

            val totalIncome = uiState.transactions
                .filter { it.type == com.personal.financeapp.domain.model.TransactionType.INCOME }
                .sumOf { it.amount }
            val totalExpense = uiState.transactions
                .filter { it.type == com.personal.financeapp.domain.model.TransactionType.EXPENSE }
                .sumOf { it.amount }

            IncomeExpenseRow(
                income = totalIncome,
                expense = totalExpense,
                currencyFormatter = currencyFormatter
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.expensesByCategory.isNotEmpty()) {
                Text(
                    text = "📊 Gastos por Categoria (Mês Atual)",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                PieChartComponent(data = uiState.expensesByCategory)
                Spacer(modifier = Modifier.height(24.dp))
            }

            if (uiState.activeGoals.isNotEmpty()) {
                Text(
                    text = "🎯 Metas Ativas",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                uiState.activeGoals.forEach { goal ->
                    GoalCard(goal = goal)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (uiState.alerts.isNotEmpty()) {
                Text(
                    text = "⚠️ Alertas Pendentes",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                uiState.alerts.forEach { alert ->
                    AlertCard(
                        alert = alert,
                        onPostpone = { viewModel.onEvent(DashboardEvent.PostponeAlert(alert.id)) },
                        onComplete = { viewModel.onEvent(DashboardEvent.CompleteAlert(alert.id)) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else {
                Text(
                    text = "⚠️ Nenhum alerta pendente",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

