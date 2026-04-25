package com.personal.financeapp.domain.usecase

import com.personal.financeapp.domain.model.Category
import com.personal.financeapp.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SeedDatabaseUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke() {
        val categories = repository.getAllCategories().first()
        if (categories.isEmpty()) {
            val defaultCategories = listOf(
                Category(1, "Alimentação", "restaurant", "#FF9800"),
                Category(2, "Transporte", "directions_car", "#2196F3"),
                Category(3, "Moradia", "home", "#E91E63"),
                Category(4, "Saúde", "medical_services", "#4CAF50"),
                Category(5, "Lazer", "sports_esports", "#9C27B0"),
                Category(6, "Salário", "payments", "#009688"),
                Category(7, "Educação", "school", "#3F51B5"),
                Category(8, "Outros", "more_horiz", "#607D8B")
            )
            repository.insertCategories(defaultCategories)
        }
    }
}
