package com.personal.financeapp.domain.repository

import com.personal.financeapp.domain.model.Category
import com.personal.financeapp.domain.model.Transaction
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    
    suspend fun getTransactionById(id: Long): Transaction?
    
    fun getTransactionsByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Transaction>>
    
    suspend fun insertTransaction(transaction: Transaction): Long
    
    suspend fun updateTransaction(transaction: Transaction)
    
    suspend fun deleteTransaction(transaction: Transaction)
    
    fun getAllCategories(): Flow<List<Category>>
    
    suspend fun insertCategories(categories: List<Category>)
}
