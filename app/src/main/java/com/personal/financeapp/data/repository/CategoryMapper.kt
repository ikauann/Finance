package com.personal.financeapp.data.repository

import com.personal.financeapp.domain.model.Category

object CategoryMapper {
    private val categoryMap = mapOf(
        "Alimentação" to Category(id = 1, name = "Alimentação", icon = "restaurant", color = "#FF9800"),
        "Transporte" to Category(id = 2, name = "Transporte", icon = "directions_car", color = "#2196F3"),
        "Educação" to Category(id = 3, name = "Educação", icon = "school", color = "#9C27B0"),
        "Saúde" to Category(id = 4, name = "Saúde", icon = "medical_services", color = "#F44336"),
        "Lazer" to Category(id = 5, name = "Lazer", icon = "movie", color = "#E91E63"),
        "Moradia" to Category(id = 6, name = "Moradia", icon = "home", color = "#795548"),
        "Compras" to Category(id = 7, name = "Compras", icon = "shopping_cart", color = "#4CAF50"),
        "Outros" to Category(id = 8, name = "Outros", icon = "more_horiz", color = "#607D8B"),
        "Transferência" to Category(id = 8, name = "Transferência", icon = "swap_horiz", color = "#607D8B"),
        "Serviços" to Category(id = 9, name = "Serviços", icon = "build", color = "#00BCD4"),
        "Renda" to Category(id = 10, name = "Renda", icon = "attach_money", color = "#4CAF50")
    )

    fun mapPluggyCategory(pluggyCategory: String?): Category {
        if (pluggyCategory == null) return categoryMap["Outros"]!!
        
        // Tenta encontrar uma correspondência direta ou aproximada
        return categoryMap.entries.find { 
            pluggyCategory.contains(it.key, ignoreCase = true) || it.key.contains(pluggyCategory, ignoreCase = true)
        }?.value ?: categoryMap["Outros"]!!
    }
}
