package com.personal.financeapp.domain.repository

import com.personal.financeapp.domain.model.Goal
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    fun getAllGoals(): Flow<List<Goal>>
    suspend fun insertGoal(goal: Goal): Long
    suspend fun updateGoal(goal: Goal)
    suspend fun deleteGoal(goal: Goal)
    suspend fun addValueToGoal(goalId: Long, amount: Double)
    suspend fun resetAllData()
}
