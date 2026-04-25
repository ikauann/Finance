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
    val isActive: Boolean = true
)
