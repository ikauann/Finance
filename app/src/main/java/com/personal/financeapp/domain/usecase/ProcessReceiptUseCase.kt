package com.personal.financeapp.domain.usecase

import com.personal.financeapp.domain.model.Transaction
import com.personal.financeapp.domain.model.TransactionType
import com.personal.financeapp.domain.repository.AiRepository
import com.personal.financeapp.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

class ProcessReceiptUseCase @Inject constructor(
    private val aiRepository: AiRepository,
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(base64Image: String): Result<Transaction> {
        return aiRepository.analyzeReceiptImage(base64Image).mapCatching { data ->
            val categories = transactionRepository.getAllCategories().first()
            val category = categories.find { 
                it.name.equals(data.categoryName, ignoreCase = true) 
            } ?: categories.find { it.id == 8L } ?: categories.first()

            Transaction(
                date = data.date?.let { 
                    try { LocalDate.parse(it) } catch (e: Exception) { null }
                } ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
                amount = data.amount,
                type = TransactionType.EXPENSE,
                category = category,
                description = data.description
            )
        }
    }
}
