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
    val isActive: Boolean = true
)
