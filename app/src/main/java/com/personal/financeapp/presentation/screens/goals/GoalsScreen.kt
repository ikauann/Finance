package com.personal.financeapp.presentation.screens.goals

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
    var showAddDialog by remember { mutableStateOf(false) }
    var goalToEdit by remember { mutableStateOf<Goal?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("Minhas Metas", fontWeight = FontWeight.Bold) 
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Nova Meta")
                    }
                }
            )
        },
        bottomBar = {
            com.personal.financeapp.presentation.components.BottomNavigationBar(navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
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
                                Text("Metas Ativas (${uiState.activeGoals.size})", fontWeight = FontWeight.Bold)
                            }
                        }
                        
                        items(uiState.activeGoals) { goal ->
                            GoalExpandedCard(
                                analyzedGoal = goal,
                                onEdit = { goalToEdit = it },
                                onDelete = { viewModel.deleteGoal(it) }
                            )
                        }
                    }

                    if (uiState.completedGoals.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Flag, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Metas Concluídas (${uiState.completedGoals.size})", fontWeight = FontWeight.Bold, color = Color.Gray)
                            }
                        }
                        
                        items(uiState.completedGoals) { goal ->
                            GoalCompletedCard(goal)
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            GoalDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { 
                    viewModel.addGoal(it)
                    showAddDialog = false
                }
            )
        }

        if (goalToEdit != null) {
            GoalDialog(
                goal = goalToEdit,
                onDismiss = { goalToEdit = null },
                onConfirm = { 
                    viewModel.updateGoal(it)
                    goalToEdit = null
                }
            )
        }
    }
}

@Composable
fun GoalExpandedCard(
    analyzedGoal: AnalyzedGoal,
    onEdit: (Goal) -> Unit,
    onDelete: (Goal) -> Unit
) {
    val goal = analyzedGoal.rawGoal
    var expanded by remember { mutableStateOf(false) }
    var showSimulation by remember { mutableStateOf(false) }

    val progress = (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f)
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                
                IconButton(onClick = { onDelete(goal) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = Color.Gray, modifier = Modifier.size(20.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            )
            
            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    DetailRow("🗓️", "Prazo", goal.targetDate)
                    DetailRow("💵", "Aporte", "${currencyFormatter.format(goal.monthlyContribution)}/mês")
                    DetailRow("🔄", "Cotação", "R$ ${String.format("%.2f", goal.exchangeRate)}/${goal.targetCurrency}")
                    DetailRow("🏳️", "Meta", goal.purpose)
                    
                    if (analyzedGoal.isShortfall) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(color = Color(0xFFFFF3E0), shape = RoundedCornerShape(8.dp)) {
                            Text(
                                text = "Atenção: Você está R$ ${String.format("%.0f", analyzedGoal.totalShortfall)} abaixo do esperado!",
                                modifier = Modifier.padding(8.dp),
                                color = Color(0xFFE65100),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { showSimulation = !showSimulation },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
                        ) {
                            Text(if (showSimulation) "Fechar Simulação" else "Simular")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(onClick = { onEdit(goal) }) {
                            Text("Editar")
                        }
                    }

                    if (showSimulation) {
                        Spacer(modifier = Modifier.height(16.dp))
                        SimulationSection(analyzedGoal)
                    }
                }
            }
        }
    }
}

@Composable
fun SimulationSection(analyzedGoal: AnalyzedGoal) {
    var extraContribution by remember { mutableStateOf("") }
    val extra = extraContribution.toDoubleOrNull() ?: 0.0
    val newMonthly = analyzedGoal.rawGoal.monthlyContribution + extra
    val newExpected = analyzedGoal.rawGoal.currentAmount + (newMonthly * analyzedGoal.remainingMonths)
    
    Column(modifier = Modifier.background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(8.dp)).padding(12.dp)) {
        Text("Simulador de Aporte Extra", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = extraContribution,
            onValueChange = { extraContribution = it },
            label = { Text("Valor extra mensal (R$)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Com R$ ${String.format("%.2f", extra)} extra, você chegará a ${analyzedGoal.rawGoal.targetCurrency} ${String.format("%.0f", newExpected / analyzedGoal.rawGoal.exchangeRate)} no final do prazo.",
            fontSize = 13.sp
        )
    }
}

@Composable
fun DetailRow(icon: String, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(goal.icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(goal.name, fontWeight = FontWeight.Bold)
                Text("Concluída!", color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDialog(
    goal: Goal? = null,
    onDismiss: () -> Unit,
    onConfirm: (Goal) -> Unit
) {
    var name by remember { mutableStateOf(goal?.name ?: "") }
    var targetAmount by remember { mutableStateOf(goal?.targetAmount?.toString() ?: "") }
    var targetCurrency by remember { mutableStateOf(goal?.targetCurrency ?: "BRL") }
    var targetDate by remember { mutableStateOf(goal?.targetDate ?: "") }
    var monthlyContribution by remember { mutableStateOf(goal?.monthlyContribution?.toString() ?: "") }
    var purpose by remember { mutableStateOf(goal?.purpose ?: "") }
    var exchangeRate by remember { mutableStateOf(goal?.exchangeRate?.toString() ?: "1.0") }
    var icon by remember { mutableStateOf(goal?.icon ?: "🎯") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (goal == null) "Nova Meta" else "Editar Meta") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                TextField(value = name, onValueChange = { name = it }, label = { Text("Nome da Meta (ex: Viagem Suíça)") })
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    TextField(
                        value = targetAmount, 
                        onValueChange = { targetAmount = it }, 
                        label = { Text("Valor Alvo") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    var expandedCurrency by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.weight(0.5f)) {
                        OutlinedButton(onClick = { expandedCurrency = true }, modifier = Modifier.fillMaxWidth()) {
                            Text(targetCurrency)
                        }
                        DropdownMenu(expanded = expandedCurrency, onDismissRequest = { expandedCurrency = false }) {
                            listOf("BRL", "USD", "CHF", "EUR", "GBP").forEach {
                                DropdownMenuItem(text = { Text(it) }, onClick = { targetCurrency = it; expandedCurrency = false })
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = targetDate, onValueChange = { targetDate = it }, label = { Text("Prazo (dd/mm/aaaa)") })
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = monthlyContribution, 
                    onValueChange = { monthlyContribution = it }, 
                    label = { Text("Aporte Mensal (R$)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = exchangeRate, 
                    onValueChange = { exchangeRate = it }, 
                    label = { Text("Cotação (1 ${targetCurrency} = ? BRL)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = purpose, onValueChange = { purpose = it }, label = { Text("Objetivo/País") })
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = icon, onValueChange = { icon = it }, label = { Text("Emoji/Ícone") })
            }
        },
        confirmButton = {
            Button(onClick = {
                val currentTargetAmount = targetAmount.toDoubleOrNull() ?: 0.0
                val currentMonthlyContribution = monthlyContribution.toDoubleOrNull() ?: 0.0
                val currentExchangeRate = exchangeRate.toDoubleOrNull() ?: 1.0

                val newGoal = goal?.copy(
                    name = name,
                    targetAmount = currentTargetAmount,
                    targetCurrency = targetCurrency,
                    targetDate = targetDate,
                    monthlyContribution = currentMonthlyContribution,
                    purpose = purpose,
                    exchangeRate = currentExchangeRate,
                    icon = icon
                ) ?: Goal(
                    name = name,
                    targetAmount = currentTargetAmount,
                    targetCurrency = targetCurrency,
                    targetDate = targetDate,
                    monthlyContribution = currentMonthlyContribution,
                    purpose = purpose,
                    exchangeRate = currentExchangeRate,
                    icon = icon
                )
                onConfirm(newGoal)
            }) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
