package com.personal.financeapp.presentation.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personal.financeapp.domain.model.Transaction
import com.personal.financeapp.domain.usecase.AddTransactionUseCase
import com.personal.financeapp.domain.usecase.GetCategoriesUseCase
import com.personal.financeapp.domain.usecase.GetTransactionsUseCase
import com.personal.financeapp.domain.usecase.SeedDatabaseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

import java.time.LocalDate
import java.time.YearMonth

@HiltViewModel
class DashboardViewModel @Inject constructor(
    getTransactionsUseCase: GetTransactionsUseCase,
    getCategoriesUseCase: GetCategoriesUseCase,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val seedDatabaseUseCase: SeedDatabaseUseCase,
    getActiveAlertsUseCase: com.personal.financeapp.domain.usecase.GetActiveAlertsUseCase,
    private val completeAlertUseCase: com.personal.financeapp.domain.usecase.CompleteAlertUseCase,
    private val postponeAlertUseCase: com.personal.financeapp.domain.usecase.PostponeAlertUseCase,
    private val vehicleAlertRepository: com.personal.financeapp.domain.repository.VehicleAlertRepository,
    private val goalRepository: com.personal.financeapp.domain.repository.GoalRepository
) : ViewModel() {

    init {
        // Seeding desativado para manter o app totalmente vazio
        // onEvent(DashboardEvent.SeedDatabase)
    }

    private val _isLoading = MutableStateFlow(false)

    val uiState: StateFlow<DashboardUiState> = combine(
        getTransactionsUseCase(),
        getCategoriesUseCase(),
        getActiveAlertsUseCase(),
        goalRepository.getAllGoals(),
        _isLoading
    ) { transactions: List<Transaction>, categories: List<com.personal.financeapp.domain.model.Category>, alerts: List<com.personal.financeapp.domain.model.VehicleAlert>, goals: List<com.personal.financeapp.domain.model.Goal>, isLoading: Boolean ->
        
        val currentYearMonth = YearMonth.now()
        val previousYearMonth = currentYearMonth.minusMonths(1)

        val balanceUpToLastMonth = transactions.filter {
            try {
                YearMonth.of(it.date.year, it.date.monthValue) <= previousYearMonth
            } catch (e: Exception) {
                false
            }
        }.sumOf { if (it.type == com.personal.financeapp.domain.model.TransactionType.INCOME) it.amount else -it.amount }

        val currentTotalBalance = transactions.sumOf { 
            if (it.type == com.personal.financeapp.domain.model.TransactionType.INCOME) it.amount else -it.amount 
        }

        val variation = if (balanceUpToLastMonth != 0.0) {
            ((currentTotalBalance - balanceUpToLastMonth) / Math.abs(balanceUpToLastMonth)) * 100.0
        } else {
            0.0
        }
        
        val variationStr = if (variation >= 0) {
            "↑ +${String.format("%.0f", variation)}% vs mês anterior"
        } else {
            "↓ ${String.format("%.0f", variation)}% vs mês anterior"
        }

        DashboardUiState(
            transactions = transactions,
            categories = categories,
            alerts = alerts,
            activeGoals = goals,
            totalBalance = currentTotalBalance,
            balanceVariationString = variationStr,
            expensesByCategory = transactions
                .filter { it.type == com.personal.financeapp.domain.model.TransactionType.EXPENSE }
                .groupBy { it.category.name }
                .mapValues { entry -> entry.value.sumOf { it.amount } },
            isLoading = isLoading
        )
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.SeedDatabase -> {
                viewModelScope.launch {
                    seedDatabaseUseCase()
                }
            }
            is DashboardEvent.ResetDatabase -> {
                viewModelScope.launch {
                    seedDatabaseUseCase.reset()
                }
            }
            is DashboardEvent.AddTransaction -> {
                viewModelScope.launch {
                    addTransactionUseCase(event.transaction)
                }
            }
            is DashboardEvent.CompleteAlert -> {
                viewModelScope.launch {
                    completeAlertUseCase(event.alertId)
                }
            }
            is DashboardEvent.PostponeAlert -> {
                viewModelScope.launch {
                    postponeAlertUseCase(event.alertId)
                }
            }
            is DashboardEvent.AddAlert -> {
                viewModelScope.launch {
                    vehicleAlertRepository.insertAlert(event.alert)
                }
            }
        }
    }
}

data class DashboardUiState(
    val transactions: List<Transaction> = emptyList(),
    val categories: List<com.personal.financeapp.domain.model.Category> = emptyList(),
    val alerts: List<com.personal.financeapp.domain.model.VehicleAlert> = emptyList(),
    val activeGoals: List<com.personal.financeapp.domain.model.Goal> = emptyList(),
    val totalBalance: Double = 0.0,
    val balanceVariationString: String = "0% vs mês anterior",
    val expensesByCategory: Map<String, Double> = emptyMap(),
    val isLoading: Boolean = false
)
