package com.personal.financeapp.domain.model

import java.time.LocalDateTime

data class Transaction(
    val id: Long = 0,
    val date: LocalDateTime,
    val amount: Double,
    val type: TransactionType,
    val category: Category,
    val subcategory: Subcategory? = null,
    val description: String? = null,
    val tags: List<String> = emptyList(),
    val receiptImagePath: String? = null
)

enum class TransactionType {
    INCOME, EXPENSE
}

data class Category(
    val id: Long,
    val name: String,
    val icon: String,
    val color: String
)

data class Subcategory(
    val id: Long,
    val categoryId: Long,
    val name: String
)
