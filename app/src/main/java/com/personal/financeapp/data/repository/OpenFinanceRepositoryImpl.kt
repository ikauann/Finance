package com.personal.financeapp.data.repository

import com.personal.financeapp.data.remote.api.OpenFinanceApi
import com.personal.financeapp.data.remote.dto.ConnectTokenOptions
import com.personal.financeapp.data.remote.dto.ConnectTokenRequest
import com.personal.financeapp.domain.model.Category
import com.personal.financeapp.domain.model.Transaction
import com.personal.financeapp.domain.model.TransactionType
import com.personal.financeapp.domain.repository.OpenFinanceRepository
import kotlinx.datetime.toLocalDate
import javax.inject.Inject

class OpenFinanceRepositoryImpl @Inject constructor(
    private val api: OpenFinanceApi
) : OpenFinanceRepository {

    private val PLUGGY_API_KEY = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJkYXRhIjoiZmUzYjdmODg1YzFlYzI2YTk1MDg1MzVhMGIxM2RmNTI6YTI0NjU4MTZlYzEzYzdiNWE1NDI2NWEzNjI0YzQ0NzE2YzY5ZDE0ZDA3ZTc2MTIyNDU1MDFjODg3MzQ3ZWU0N2EwMWJjY2Q5YmIzOWRlNDQwNTYwZjQ2ZTBmNzRiNzI0MmMxNjQwN2ZlNWY5ODdiZmU1OTIwMWUwMTFjYmEwOTU5Y2E1NjI3NjAzMzk5NWI2MTk2NmQxMjFiZDI0MTIxZCIsImlhdCI6MTc3NzE0NTI2MCwiZXhwIjoxNzc3MTUyNDYwfQ.PxNThFcHVC5qF7kfhGdYrJny1ElGl4b3wSv5Wle5Fp-U-GBNyT9Tl7Q8DtDSKpCxLABPZzZcJGeEpZsgGVJWrXi1q_tgREUa6vrPgqcVuxhsZT33q9OAaN2s-B8YB3DxiEBGWiJcpUWOP29_HxPY8hzjakn8P3HwhR-XjohWf7Utw_BOWf-ElhU2Afaeh7ZPyf9kM0Gu6tsStm8HSJdLiDgzf1lZsgGKcGI5uRtUZ27jIKxaWtGAJwd-_4v_azw0De8yJmN89dSaP1bhvMBUXEVXOaMwyXjkKD6jc12wJb7IQbUfSUaKqXsNTjz3WCcFyB0VFAYdrkPKSHZVGbAb0w"

    override suspend fun getConnectWidgetUrl(): Result<String> {
        return try {
            val request = ConnectTokenRequest(
                options = ConnectTokenOptions(
                    clientName = "Finance App",
                    productFilters = listOf("ACCOUNTS", "TRANSACTIONS"),
                    androidRedirect = "financeapp://callback"
                )
            )
            val response = api.createConnectToken(PLUGGY_API_KEY, request)
            val url = response.connectUrl ?: "https://connect.pluggy.ai/?token=${response.accessToken}"
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncAccountTransactions(itemId: String): Result<List<Transaction>> {
        return try {
            val pluggyTransactions = api.getTransactions(PLUGGY_API_KEY, itemId)
            val transactions = pluggyTransactions.map { pt ->
                Transaction(
                    date = pt.date.substring(0, 10).toLocalDate(),
                    amount = Math.abs(pt.amount),
                    type = if (pt.amount < 0) TransactionType.EXPENSE else TransactionType.INCOME,
                    description = pt.description,
                    category = Category(
                        id = 8, // "Outros" - Garantindo que o ID existe no banco
                        name = pt.category ?: "Outros",
                        icon = "more_horiz",
                        color = "#607D8B"
                    )
                )
            }
            Result.success(transactions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
