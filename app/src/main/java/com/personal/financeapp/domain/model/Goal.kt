package com.personal.financeapp.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Goal(
    val id: Long = 0,
    val name: String,
    val targetAmount: Double,
    val targetCurrency: String,
    val targetDate: String,
    val currentAmount: Double = 0.0,
    val isActive: Boolean = true,
    val priority: String = "Média",
    val monthlyContribution: Double = 0.0,
    val purpose: String = "",
    val exchangeRate: Double = 1.0,
    val icon: String = "🎯",
    val color: String = "#2196F3",
    val historicalContributions: List<Double> = emptyList(),
    val isShortfall: Boolean = false,
    val shortfallAmount: Double = 0.0,
    val shortfallRecommendation: String = ""
)
