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

@HiltViewModel
class DashboardViewModel @Inject constructor(
    getTransactionsUseCase: GetTransactionsUseCase,
    getCategoriesUseCase: GetCategoriesUseCase,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val seedDatabaseUseCase: SeedDatabaseUseCase
) : ViewModel() {

    init {
        onEvent(DashboardEvent.SeedDatabase)
    }

    private val _isLoading = MutableStateFlow(false)

    val uiState: StateFlow<DashboardUiState> = combine(
        getTransactionsUseCase(),
        getCategoriesUseCase(),
        _isLoading
    ) { transactions, categories, isLoading ->
        DashboardUiState(
            transactions = transactions,
            categories = categories,
            totalBalance = transactions.sumOf { 
                if (it.type == com.personal.financeapp.domain.model.TransactionType.INCOME) it.amount else -it.amount 
            },
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
            is DashboardEvent.AddTransaction -> {
                viewModelScope.launch {
                    addTransactionUseCase(event.transaction)
                }
            }
        }
    }
}

data class DashboardUiState(
    val transactions: List<Transaction> = emptyList(),
    val categories: List<com.personal.financeapp.domain.model.Category> = emptyList(),
    val totalBalance: Double = 0.0,
    val expensesByCategory: Map<String, Double> = emptyMap(),
    val isLoading: Boolean = false
)
