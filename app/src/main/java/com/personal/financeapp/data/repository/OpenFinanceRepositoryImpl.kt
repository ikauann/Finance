package com.personal.financeapp.data.repository

import com.personal.financeapp.data.local.dao.ConnectedItemDao
import com.personal.financeapp.data.local.dao.CategoryDao
import com.personal.financeapp.data.local.entity.ConnectedItemEntity
import com.personal.financeapp.data.local.entity.CategoryEntity
import com.personal.financeapp.data.remote.api.OpenFinanceApi
import android.util.Log
import com.personal.financeapp.data.remote.dto.*
import com.personal.financeapp.domain.model.Transaction
import com.personal.financeapp.domain.model.TransactionType
import com.personal.financeapp.domain.repository.OpenFinanceRepository
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.Instant
import javax.inject.Inject

class OpenFinanceRepositoryImpl @Inject constructor(
    private val api: OpenFinanceApi,
    private val connectedItemDao: ConnectedItemDao,
    private val categoryDao: CategoryDao
) : OpenFinanceRepository {

    private val CLIENT_ID = "13895361-dc76-4bb7-a60b-7c4c2043a43f".trim()
    private val CLIENT_SECRET = "6c12f61d-b24d-4621-b7e1-86668229493e".trim()
    
    private var PLUGGY_API_KEY = ""

    private suspend fun reauthenticate() {
        val authResponse = api.authenticate(AuthRequest(CLIENT_ID, CLIENT_SECRET))
        PLUGGY_API_KEY = authResponse.apiKey
        Log.d("PLUGGY_DEBUG", "API Key re-autenticada com sucesso")
    }

    override suspend fun getConnectToken(): Result<String> {
        return try {
            reauthenticate()

            val request = ConnectTokenRequest(
                clientUserId = "user-finance-app",
                options = ConnectTokenOptions(
                    clientName = "Finance App",
                    oauthRedirectUri = "financeapp://callback"
                )
            )
            val response = api.createConnectToken(PLUGGY_API_KEY, request)
            
            val token = (response.accessToken ?: response.connectToken)?.trim() 
                ?: throw Exception("Token não retornado pela API")

            Log.d("PLUGGY_DEBUG", "Connect token obtido: ${token.take(20)}...")
            
            Result.success(token)
        } catch (e: Exception) {
            Log.e("PLUGGY_DEBUG", "Erro ao obter connect token", e)
            Result.failure(e)
        }
    }

    override suspend fun saveAndSyncItem(itemId: String): Result<List<Transaction>> {
        return try {
            reauthenticate()

            // 1. Buscar dados do Item na Pluggy (com polling para status UPDATED)
            val item = waitForItemReady(itemId)
            Log.d("PLUGGY_DEBUG", "Item pronto: status=${item.status}, connector=${item.connector?.name}")

            // 2. Salvar a conexão localmente
            val now = Instant.now().toString()
            connectedItemDao.upsertItem(
                ConnectedItemEntity(
                    itemId = item.id,
                    connectorName = item.connector?.name ?: "Banco desconhecido",
                    connectorId = item.connector?.id ?: 0,
                    status = item.status,
                    connectedAt = item.createdAt ?: now,
                    lastSyncAt = now
                )
            )
            Log.d("PLUGGY_DEBUG", "Conexão salva no banco local: ${item.connector?.name}")

            // 3. Garantir que as categorias existem no Room (previne crash de FK)
            ensureCategoriesExist()

            // 4. Buscar contas do Item
            val accountsResponse = api.getAccounts(PLUGGY_API_KEY, itemId)
            Log.d("PLUGGY_DEBUG", "Contas encontradas: ${accountsResponse.total}")

            if (accountsResponse.results.isEmpty()) {
                return Result.success(emptyList())
            }

            // 5. Buscar transações de CADA conta
            val allTransactions = mutableListOf<Transaction>()
            for (account in accountsResponse.results) {
                Log.d("PLUGGY_DEBUG", "Buscando transações da conta: ${account.name} (${account.id})")
                try {
                    val txResponse = api.getTransactions(PLUGGY_API_KEY, account.id)
                    val transactions = txResponse.results.map { pt ->
                        val dateStr = pt.date.substring(0, 10)
                        Transaction(
                            date = try { LocalDateTime.parse("${dateStr}T12:00:00") } catch(e: Exception) { LocalDateTime.now() },
                            amount = Math.abs(pt.amount),
                            type = if (pt.amount < 0) TransactionType.EXPENSE else TransactionType.INCOME,
                            description = pt.description,
                            category = CategoryMapper.mapPluggyCategory(pt.category)
                        )
                    }
                    allTransactions.addAll(transactions)
                    Log.d("PLUGGY_DEBUG", "Conta ${account.name}: ${transactions.size} transações")
                } catch (e: Exception) {
                    Log.w("PLUGGY_DEBUG", "Erro ao buscar transações da conta ${account.id}: ${e.message}")
                }
            }

            // 6. Atualizar status de sync
            connectedItemDao.updateSyncStatus(itemId, "SYNCED", Instant.now().toString())

            Result.success(allTransactions)
        } catch (e: Exception) {
            Log.e("PLUGGY_DEBUG", "Erro ao sincronizar item $itemId", e)
            Result.failure(e)
        }
    }

    /**
     * Garante que as categorias do CategoryMapper existem no Room.
     * Isso previne o crash de FOREIGN KEY quando inserimos transações.
     */
    private suspend fun ensureCategoriesExist() {
        val categories = listOf(
            CategoryEntity(id = 1, name = "Alimentação", icon = "restaurant", color = "#FF9800"),
            CategoryEntity(id = 2, name = "Transporte", icon = "directions_car", color = "#2196F3"),
            CategoryEntity(id = 3, name = "Educação", icon = "school", color = "#9C27B0"),
            CategoryEntity(id = 4, name = "Saúde", icon = "medical_services", color = "#F44336"),
            CategoryEntity(id = 5, name = "Lazer", icon = "movie", color = "#E91E63"),
            CategoryEntity(id = 6, name = "Moradia", icon = "home", color = "#795548"),
            CategoryEntity(id = 7, name = "Compras", icon = "shopping_cart", color = "#4CAF50"),
            CategoryEntity(id = 8, name = "Outros", icon = "more_horiz", color = "#607D8B"),
            CategoryEntity(id = 9, name = "Serviços", icon = "build", color = "#00BCD4"),
            CategoryEntity(id = 10, name = "Renda", icon = "attach_money", color = "#4CAF50")
        )
        categoryDao.insertCategories(categories)
        Log.d("PLUGGY_DEBUG", "Categorias garantidas no Room")
    }

    /**
     * Aguarda o Item ficar pronto (status UPDATED). 
     * Faz polling a cada 3s, até 10 tentativas (30s max).
     */
    private suspend fun waitForItemReady(itemId: String): PluggyItemResponse {
        repeat(10) { attempt ->
            val item = api.getItem(PLUGGY_API_KEY, itemId)
            Log.d("PLUGGY_DEBUG", "Polling item ($attempt): status=${item.status}")

            when (item.status) {
                "UPDATED" -> return item
                "LOGIN_ERROR" -> throw Exception("Erro de login no banco: ${item.error?.message ?: "credenciais inválidas"}")
                "OUTDATED" -> throw Exception("Conexão expirada. Reconecte o banco.")
                "WAITING_USER_INPUT", "WAITING_USER_ACTION" -> throw Exception("O banco requer ação adicional. Tente novamente.")
                "UPDATING" -> {
                    delay(3000)
                }
                else -> {
                    delay(3000)
                }
            }
        }
        throw Exception("Timeout: item não ficou pronto em 30 segundos. Tente novamente em instantes.")
    }

    override suspend fun getConnectedItems(): List<ConnectedItemEntity> {
        // TODO: retornar via Flow quando necessário
        return emptyList()
    }
}
