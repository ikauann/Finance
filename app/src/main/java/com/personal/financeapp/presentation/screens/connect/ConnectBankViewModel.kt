package com.personal.financeapp.presentation.screens.connect

import android.util.Log
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
            openFinanceRepository.getConnectToken()
                .onSuccess { token ->
                    _uiState.value = ConnectBankUiState.ReadyToConnect(
                        connectToken = token,
                        includeSandbox = true
                    )
                }
                .onFailure { error ->
                    _uiState.value = ConnectBankUiState.Error(error.message ?: "Erro desconhecido")
                }
        }
    }

    fun onWebViewError(message: String) {
        _uiState.value = ConnectBankUiState.Error(message)
    }

    fun onConnectionSuccess(itemId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = ConnectBankUiState.Syncing
                
                // Salvar conexão e buscar transações da Pluggy
                val result = openFinanceRepository.saveAndSyncItem(itemId)
                
                result.onSuccess { transactions ->
                    Log.d("PLUGGY_DEBUG", "Sync OK: ${transactions.size} transações obtidas")
                    
                    // Inserir cada transação individualmente, ignorando falhas de FK
                    var inserted = 0
                    for (tx in transactions) {
                        try {
                            transactionRepository.insertTransaction(tx)
                            inserted++
                        } catch (e: Exception) {
                            Log.w("PLUGGY_DEBUG", "Falha ao inserir transação '${tx.description}': ${e.message}")
                        }
                    }
                    Log.d("PLUGGY_DEBUG", "Inseridas $inserted de ${transactions.size} transações")
                    _uiState.value = ConnectBankUiState.Success
                }.onFailure { error ->
                    Log.e("PLUGGY_DEBUG", "Falha no sync: ${error.message}", error)
                    _uiState.value = ConnectBankUiState.Error(error.message ?: "Erro na sincronização")
                }
            } catch (e: Exception) {
                Log.e("PLUGGY_DEBUG", "Crash evitado em onConnectionSuccess: ${e.message}", e)
                _uiState.value = ConnectBankUiState.Error("Erro inesperado: ${e.message}")
            }
        }
    }
}

sealed class ConnectBankUiState {
    object Idle : ConnectBankUiState()
    object Loading : ConnectBankUiState()
    data class ReadyToConnect(val connectToken: String, val includeSandbox: Boolean = true) : ConnectBankUiState()
    object Syncing : ConnectBankUiState()
    object Success : ConnectBankUiState()
    data class Error(val message: String) : ConnectBankUiState()
}
