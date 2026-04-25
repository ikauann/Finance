package com.personal.financeapp.presentation.screens.connect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personal.financeapp.domain.repository.OpenFinanceRepository
import com.personal.financeapp.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectBankViewModel @Inject constructor(
    private val openFinanceRepository: OpenFinanceRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ConnectBankUiState>(ConnectBankUiState.Idle)
    val uiState: StateFlow<ConnectBankUiState> = _uiState

    fun onConnectClick() {
        viewModelScope.launch {
            _uiState.value = ConnectBankUiState.Loading
            openFinanceRepository.getConnectWidgetUrl()
                .onSuccess { url ->
                    _uiState.value = ConnectBankUiState.ReadyToConnect(url)
                }
                .onFailure { error ->
                    _uiState.value = ConnectBankUiState.Error(error.message ?: "Erro desconhecido")
                }
        }
    }

    fun onConnectionSuccess(itemId: String) {
        viewModelScope.launch {
            _uiState.value = ConnectBankUiState.Syncing
            openFinanceRepository.syncAccountTransactions(itemId)
                .onSuccess { transactions ->
                    transactions.forEach { transactionRepository.insertTransaction(it) }
                    _uiState.value = ConnectBankUiState.Success
                }
                .onFailure { error ->
                    _uiState.value = ConnectBankUiState.Error(error.message ?: "Erro na sincronização")
                }
        }
    }
}

sealed class ConnectBankUiState {
    object Idle : ConnectBankUiState()
    object Loading : ConnectBankUiState()
    data class ReadyToConnect(val url: String) : ConnectBankUiState()
    object Syncing : ConnectBankUiState()
    object Success : ConnectBankUiState()
    data class Error(val message: String) : ConnectBankUiState()
}
