package com.personal.financeapp.data.repository

import com.personal.financeapp.data.local.entity.CategoryEntity
import com.personal.financeapp.data.local.entity.TransactionEntity
import com.personal.financeapp.domain.model.Category
import com.personal.financeapp.domain.model.Transaction
import com.personal.financeapp.domain.model.TransactionType
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate

fun TransactionEntity.toDomain(category: CategoryEntity): Transaction {
    return Transaction(
        id = id,
        date = date.toLocalDate(),
        amount = amount,
        type = if (type == "income") TransactionType.INCOME else TransactionType.EXPENSE,
        category = category.toDomain(),
        description = description,
        tags = tags?.split(",")?.map { it.trim() } ?: emptyList(),
        receiptImagePath = receiptImagePath
    )
}

fun CategoryEntity.toDomain(): Category {
    return Category(
        id = id,
        name = name,
        icon = icon,
        color = color
    )
}

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        date = date.toString(),
        amount = amount,
        type = if (type == TransactionType.INCOME) "income" else "expense",
        categoryId = category.id,
        description = description,
        tags = if (tags.isEmpty()) null else tags.joinToString(","),
        receiptImagePath = receiptImagePath
    )
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        icon = icon,
        color = color
    )
}
