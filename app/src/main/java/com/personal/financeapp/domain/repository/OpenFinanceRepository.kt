package com.personal.financeapp.domain.repository

import com.personal.financeapp.data.local.entity.ConnectedItemEntity
import com.personal.financeapp.domain.model.Transaction

interface OpenFinanceRepository {
    // Retorna o connectToken para inicializar o Pluggy Connect Widget via JS SDK
    suspend fun getConnectToken(): Result<String>
    
    // Salva a conexão e sincroniza as transações de todas as contas do Item
    suspend fun saveAndSyncItem(itemId: String): Result<List<Transaction>>

    // Retorna os itens conectados salvos localmente
    suspend fun getConnectedItems(): List<ConnectedItemEntity>
}
