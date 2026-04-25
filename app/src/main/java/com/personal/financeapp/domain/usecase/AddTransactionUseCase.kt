package com.personal.financeapp.domain.usecase

import com.personal.financeapp.domain.model.Transaction
import com.personal.financeapp.domain.repository.TransactionRepository
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction): Long {
        return repository.insertTransaction(transaction)
    }
}
