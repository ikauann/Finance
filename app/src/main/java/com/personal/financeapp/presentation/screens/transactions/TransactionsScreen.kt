package com.personal.financeapp.presentation.screens.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.personal.financeapp.domain.model.Transaction
import com.personal.financeapp.presentation.navigation.Screen
import com.personal.financeapp.presentation.screens.dashboard.AddTransactionDialog
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    navController: NavController,
    viewModel: TransactionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddTransactionDialog(
            categories = uiState.categories,
            onDismiss = { showAddDialog = false },
            onConfirm = { transaction ->
                viewModel.addTransaction(transaction)
                showAddDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Transações", 
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { /* TODO Open Drawer */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO Filters */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filtro")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            com.personal.financeapp.presentation.components.BottomNavigationBar(navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                elevation = FloatingActionButtonDefaults.elevation(6.dp)
            ) {
                Icon(Icons.Default.Add, "Nova transação")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Buscar transações...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                )
            )

            // Chips
            ScrollableTabRow(
                selectedTabIndex = TransactionFilter.values().indexOf(uiState.selectedFilter),
                edgePadding = 16.dp,
                indicator = {},
                divider = {},
                containerColor = Color.Transparent,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                TransactionFilter.values().forEach { filter ->
                    val selected = uiState.selectedFilter == filter
                    Surface(
                        modifier = Modifier.padding(end = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        border = if (!selected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null,
                        onClick = { viewModel.onFilterSelect(filter) }
                    ) {
                        Text(
                            text = filter.label,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // List
            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.filteredGroupedTransactions.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhuma transação encontrada.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 80.dp), // padding for FAB
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.filteredGroupedTransactions.forEach { (dateGroup, transactions) ->
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = dateGroup,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.DarkGray
                                )
                            }
                        }

                        items(transactions) { transaction ->
                            TransactionListItem(transaction = transaction)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionListItem(transaction: Transaction) {
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        try { 
                            Color(android.graphics.Color.parseColor(transaction.category.color)).copy(alpha = 0.2f)
                        } catch (e: Exception) { 
                            MaterialTheme.colorScheme.primaryContainer 
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = transaction.category.icon.takeIf { it.length <= 2 } ?: "💰", // Emoji fallback
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Body
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = transaction.description ?: "Sem descrição",
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    val amountStr = currencyFormatter.format(transaction.amount)
                    Text(
                        text = if (transaction.type == com.personal.financeapp.domain.model.TransactionType.INCOME) "+$amountStr" else "-$amountStr",
                        color = if (transaction.type == com.personal.financeapp.domain.model.TransactionType.INCOME) Color(0xFF2E7D32) else Color(0xFFC62828),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${transaction.category.name}" + (transaction.subcategory?.let { " • ${it.name}" } ?: ""),
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = transaction.date.format(timeFormatter),
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                if (transaction.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        transaction.tags.forEach { tag ->
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = tag,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
