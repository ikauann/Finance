package com.personal.financeapp.domain.repository

import com.personal.financeapp.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface OpenFinanceRepository {
    // Retorna a URL para o usuário abrir o Widget de conexão do banco
    suspend fun getConnectWidgetUrl(): Result<String>
    
    // Sincroniza as transações de uma conta conectada
    suspend fun syncAccountTransactions(accountId: String): Result<List<Transaction>>
}
