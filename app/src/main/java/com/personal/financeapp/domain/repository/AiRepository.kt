package com.personal.financeapp.domain.repository

import com.personal.financeapp.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface AiRepository {
    suspend fun analyzeReceiptImage(base64Image: String): Result<TransactionData>
    suspend fun askAssistant(prompt: String, context: String): Result<String>
}

data class TransactionData(
    val amount: Double,
    val description: String,
    val date: String?,
    val categoryName: String?
)
