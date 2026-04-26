package com.personal.financeapp.presentation.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personal.financeapp.domain.model.Transaction
import com.personal.financeapp.domain.model.TransactionType
import com.personal.financeapp.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.personal.financeapp.domain.usecase.AddTransactionUseCase
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val addTransactionUseCase: AddTransactionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()

    private var allTransactions: List<Transaction> = emptyList()

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            launch {
                repository.getAllCategories().collect { cats ->
                    _uiState.update { it.copy(categories = cats) }
                }
            }
            launch {
                repository.getAllTransactions()
                    .onStart { _uiState.update { it.copy(isLoading = true) } }
                    .collect { transactions ->
                        allTransactions = transactions
                        applyFilters()
                    }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun onFilterSelect(filter: TransactionFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
        applyFilters()
    }

    private fun applyFilters() {
        val currentState = _uiState.value
        val query = currentState.searchQuery.lowercase(Locale.getDefault())
        
        var filteredList = allTransactions

        // Apply Search
        if (query.isNotBlank()) {
            filteredList = filteredList.filter {
                it.description?.lowercase(Locale.getDefault())?.contains(query) == true ||
                it.category.name.lowercase(Locale.getDefault()).contains(query) ||
                it.tags.any { tag -> tag.lowercase(Locale.getDefault()).contains(query) }
            }
        }

        // Apply Chips
        filteredList = when (currentState.selectedFilter) {
            TransactionFilter.ALL -> filteredList
            TransactionFilter.INCOME -> filteredList.filter { it.type == TransactionType.INCOME }
            TransactionFilter.EXPENSE -> filteredList.filter { it.type == TransactionType.EXPENSE }
            TransactionFilter.THIS_MONTH -> {
                val currentMonth = YearMonth.now()
                filteredList.filter { YearMonth.from(it.date) == currentMonth }
            }
        }

        // Group by Date String
        val grouped = filteredList
            .sortedByDescending { it.date }
            .groupBy { tx ->
                val today = LocalDate.now()
                val yesterday = today.minusDays(1)
                val txDate = tx.date.toLocalDate()
                
                val dateStr = txDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("pt", "BR")))
                when (txDate) {
                    today -> "Hoje - $dateStr"
                    yesterday -> "Ontem - $dateStr"
                    else -> dateStr
                }
            }

        _uiState.update {
            it.copy(
                transactions = filteredList,
                filteredGroupedTransactions = grouped,
                isLoading = false
            )
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            addTransactionUseCase(transaction)
        }
    }
}

data class TransactionsUiState(
    val transactions: List<Transaction> = emptyList(),
    val filteredGroupedTransactions: Map<String, List<Transaction>> = emptyMap(),
    val categories: List<com.personal.financeapp.domain.model.Category> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: TransactionFilter = TransactionFilter.ALL,
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class TransactionFilter(val label: String) {
    ALL("Todas"),
    INCOME("Receitas"),
    EXPENSE("Despesas"),
    THIS_MONTH("Este Mês")
}
