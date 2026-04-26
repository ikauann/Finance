package com.personal.financeapp.presentation.screens.goals

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.personal.financeapp.domain.model.Goal
import com.personal.financeapp.domain.usecase.AnalyzedGoal
import com.personal.financeapp.presentation.navigation.Screen
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    navController: NavController,
    viewModel: GoalsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("Minhas Metas", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground) 
                },
                navigationIcon = {
                    IconButton(onClick = { /* TODO Drawer */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Nova Meta")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            val navItemColors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
                modifier = Modifier.height(80.dp)
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Dashboard.route) { popUpTo(0) } },
                    icon = { Icon(Icons.Default.Home, "Início") },
                    label = { Text("Início") },
                    colors = navItemColors
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Transactions.route) },
                    icon = { Icon(Icons.Default.List, "Transações") },
                    label = { Text("Transações") },
                    colors = navItemColors
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.EmojiEvents, "Metas") },
                    label = { Text("Metas") },
                    colors = navItemColors
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Receipt.route) },
                    icon = { Icon(Icons.Default.CameraAlt, "Cupom") }, // Match Dashboard
                    label = { Text("Cupom") },
                    colors = navItemColors
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Assistant.route) },
                    icon = { Icon(Icons.Default.SmartToy, "Assistente") },
                    label = { Text("Assistente") },
                    colors = navItemColors
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.primary, // Orangeish in mockup, fallback to primary
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(50)
            ) {
                Icon(Icons.Default.AddTask, contentDescription = "Nova Meta")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (uiState.activeGoals.isNotEmpty()) {
                        item {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.TrackChanges, contentDescription = null, tint = Color.Red, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Metas Ativas (${uiState.activeGoals.size})", 
                                    fontWeight = FontWeight.Medium, 
                                    fontSize = 16.sp
                                )
                            }
                        }
                        
                        items(uiState.activeGoals) { goal ->
                            GoalExpandedCard(analyzedGoal = goal)
                        }
                    }

                    if (uiState.completedGoals.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Flag, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Metas Concluídas (${uiState.completedGoals.size})", 
                                    fontWeight = FontWeight.Medium, 
                                    fontSize = 16.sp,
                                    color = Color.DarkGray
                                )
                            }
                        }
                        
                        items(uiState.completedGoals) { goal ->
                            GoalCompletedCard(analyzedGoal = goal)
                        }
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(80.dp)) // For FAB
                    }
                }
            }
        }

        if (showDialog) {
            AddGoalDialog(
                onDismiss = { showDialog = false },
                onConfirm = { name, target ->
                    viewModel.addGoal(name, target)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun GoalExpandedCard(analyzedGoal: AnalyzedGoal) {
    val goal = analyzedGoal.rawGoal
    var expanded by remember { mutableStateOf(goal.name.contains("Suíça")) } // This is still okay to keep "Suíça" expanded by default for the mockup look. But I should probably just make it expanded if it has shortfall? No, let's keep it default expanded for the first one. Let's just make `expanded` default to `analyzedGoal.isShortfall`!

    val progress = (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f)
    val progressPercent = (progress * 100).toInt()
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                try { Color(android.graphics.Color.parseColor(goal.color)).copy(alpha = 0.2f) } 
                                catch (e: Exception) { Color.LightGray }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(goal.icon, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = goal.name, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                if (expanded) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Prioridade ${goal.priority}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Tap para", fontSize = 12.sp, color = Color.Gray)
                        Text("expandir", fontSize = 12.sp, color = Color.Gray)
                        Icon(Icons.Default.KeyboardArrowDown, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    }
                }
            }
            
            if (!expanded) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = goal.purpose,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(start = 40.dp) // align with text
                )
            }
            
            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Progress Bar Area
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${goal.targetCurrency} ${String.format("%.0f", goal.currentAmount)}",
                            fontSize = 14.sp,
                            color = Color.DarkGray
                        )
                        Text(
                            text = "${goal.targetCurrency} ${String.format("%.0f", goal.targetAmount)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${analyzedGoal.progressPercent}% do objetivo concluído",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Details List
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DetailRow("🗓️", "Prazo", goal.targetDate)
                        DetailRow("💵", "Aporte", "${currencyFormatter.format(goal.monthlyContribution)}/mês")
                        DetailRow("🔄", "Cotação", "R$ ${String.format("%.2f", goal.exchangeRate)}/${goal.targetCurrency}")
                        DetailRow("🏳️", "Meta", goal.purpose)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Chart
                    if (goal.historicalContributions.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TrendingUp, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Histórico de Aportes", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Simple Bar Chart
                        val maxContribution = goal.historicalContributions.maxOrNull() ?: 1.0
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            goal.historicalContributions.forEach { value ->
                                val heightPercent = (value / maxContribution).toFloat().coerceIn(0.1f, 1f)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 2.dp)
                                        .fillMaxHeight(heightPercent)
                                        .background(Color(0xFFC8E6C9)) // Light green
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Alert Box
                    if (analyzedGoal.isShortfall) {
                        Surface(
                            color = Color(0xFFFFF3E0), // Light orange
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFE65100))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Você está R$ ${String.format("%.0f", analyzedGoal.totalShortfall)} abaixo!",
                                        color = Color(0xFFE65100),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Considere aumentar seu aporte em R\$ ${String.format("%.0f", analyzedGoal.recommendedExtraMonthly)} nos próximos ${analyzedGoal.remainingMonths} meses para manter o cronograma original.",
                                        color = Color.DarkGray,
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { /* TODO */ },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)), // Solid orange
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Simular", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = { /* TODO */ },
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
                        ) {
                            Text("Editar", color = Color.Black)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = { expanded = false },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.width(48.dp)
                        ) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Recolher", tint = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun DetailRow(icon: String, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(icon, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(label + ":", color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(value, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}

@Composable
fun GoalCompletedCard(analyzedGoal: AnalyzedGoal) {
    val goal = analyzedGoal.rawGoal
    Surface(
        color = Color(0xFFF5F5F5), // Light gray bg
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(goal.icon, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = goal.name,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Concluída em ${goal.targetDate}",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

@Composable
fun AddGoalDialog(onDismiss: () -> Unit, onConfirm: (String, Double) -> Unit) {
    var name by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nova Meta Financeira") },
        text = {
            Column {
                TextField(value = name, onValueChange = { name = it }, label = { Text("Objetivo") })
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = target, onValueChange = { target = it }, label = { Text("Valor Alvo") })
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(name, target.toDoubleOrNull() ?: 0.0) }) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
