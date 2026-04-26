package com.personal.financeapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val name: String,
    
    @ColumnInfo(name = "target_amount")
    val targetAmount: Double,
    
    @ColumnInfo(name = "target_currency")
    val targetCurrency: String,
    
    @ColumnInfo(name = "target_date")
    val targetDate: String,
    
    @ColumnInfo(name = "current_amount")
    val currentAmount: Double = 0.0,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,
    
    val priority: String = "Média",
    
    @ColumnInfo(name = "monthly_contribution")
    val monthlyContribution: Double = 0.0,
    
    val purpose: String = "",
    
    @ColumnInfo(name = "exchange_rate")
    val exchangeRate: Double = 1.0,
    
    val icon: String = "🎯",
    
    val color: String = "#2196F3",
    
    @ColumnInfo(name = "historical_contributions")
    val historicalContributions: String = "[]", // JSON array
    
    @ColumnInfo(name = "is_shortfall")
    val isShortfall: Boolean = false,
    
    @ColumnInfo(name = "shortfall_amount")
    val shortfallAmount: Double = 0.0,
    
    @ColumnInfo(name = "shortfall_recommendation")
    val shortfallRecommendation: String = ""
)
