package com.personal.financeapp.data.repository

import com.personal.financeapp.data.local.dao.CategoryDao
import com.personal.financeapp.data.local.dao.TransactionDao
import com.personal.financeapp.domain.model.Category
import com.personal.financeapp.domain.model.Transaction
import com.personal.financeapp.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions().combine(categoryDao.getAllCategories()) { entities, categories ->
            val categoryMap = categories.associateBy { it.id }
            entities.mapNotNull { entity ->
                categoryMap[entity.categoryId]?.let { categoryEntity ->
                    entity.toDomain(categoryEntity)
                }
            }
        }
    }

    override suspend fun getTransactionById(id: Long): Transaction? {
        val entity = transactionDao.getTransactionById(id)
        return entity?.let {
            val category = categoryDao.getCategoryById(it.categoryId)
            category?.let { cat -> it.toDomain(cat) }
        }
    }

    override fun getTransactionsByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByDateRange(startDate.toString(), endDate.toString())
            .combine(categoryDao.getAllCategories()) { entities, categories ->
                val categoryMap = categories.associateBy { it.id }
                entities.mapNotNull { entity ->
                    categoryMap[entity.categoryId]?.let { categoryEntity ->
                        entity.toDomain(categoryEntity)
                    }
                }
            }
    }

    override suspend fun insertTransaction(transaction: Transaction): Long {
        return transactionDao.insertTransaction(transaction.toEntity())
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction.toEntity())
    }

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertCategories(categories: List<Category>) {
        categoryDao.insertCategories(categories.map { it.toEntity() })
    }

    override suspend fun resetAllData() {
        transactionDao.deleteAllTransactions()
        transactionDao.deleteAllCategories()
    }
}
