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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Finance App") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.ConnectBank.createRoute(null)) }) {
                        Icon(Icons.Default.AccountBalance, "Conectar Banco")
                    }
                    IconButton(onClick = { navController.navigate(Screen.Goals.route) }) {
                        Icon(Icons.Default.EmojiEvents, "Metas")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Dashboard, "Dashboard") },
                    label = { Text("Início") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Transactions.route) },
                    icon = { Icon(Icons.Default.List, "Transações") },
                    label = { Text("Transações") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Receipt.route) },
                    icon = { Icon(Icons.Default.CameraAlt, "Cupom") },
                    label = { Text("Cupom") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Assistant.route) },
                    icon = { Icon(Icons.Default.SmartToy, "Assistente") },
                    label = { Text("Assistente") }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, "Nova transação")
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
                variation = "↑ +12% vs mês anterior", // Exemplo fixo conforme spec
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
                    text = "📊 Gastos por Categoria",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                PieChartComponent(data = uiState.expensesByCategory)
                Spacer(modifier = Modifier.height(24.dp))
            }

            Text(
                text = "Transações Recentes",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.transactions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("Nenhuma transação encontrada.", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.transactions.take(5).forEach { transaction ->
                        TransactionItem(transaction, currencyFormatter)
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: com.personal.financeapp.domain.model.Transaction,
    formatter: NumberFormat
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column {
                Text(text = transaction.category.name, style = MaterialTheme.typography.titleSmall)
                transaction.description?.let {
                    if (it.isNotEmpty()) {
                        Text(text = it, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            Text(
                text = (if (transaction.type == com.personal.financeapp.domain.model.TransactionType.EXPENSE) "-" else "+") + 
                    formatter.format(transaction.amount),
                color = if (transaction.type == com.personal.financeapp.domain.model.TransactionType.EXPENSE) 
                    MaterialTheme.colorScheme.error 
                else 
                    MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
