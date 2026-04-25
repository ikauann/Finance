package com.personal.financeapp.data.repository

import com.personal.financeapp.data.local.dao.GoalDao
import com.personal.financeapp.data.local.entity.GoalEntity
import com.personal.financeapp.domain.model.Goal
import com.personal.financeapp.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao
) : GoalRepository {

    override fun getAllGoals(): Flow<List<Goal>> {
        return goalDao.getAllGoals().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertGoal(goal: Goal) {
        goalDao.insertGoal(goal.toEntity())
    }

    override suspend fun deleteGoal(goal: Goal) {
        goalDao.deleteGoal(goal.toEntity())
    }

    override suspend fun addValueToGoal(goalId: Long, amount: Double) {
        goalDao.addValueToGoal(goalId, amount)
    }

    private fun GoalEntity.toDomain() = Goal(
        id = id,
        name = name,
        targetAmount = targetAmount,
        targetCurrency = targetCurrency,
        targetDate = targetDate,
        currentAmount = currentAmount,
        isActive = isActive
    )

    private fun Goal.toEntity() = GoalEntity(
        id = id,
        name = name,
        targetAmount = targetAmount,
        targetCurrency = targetCurrency,
        targetDate = targetDate,
        currentAmount = currentAmount,
        isActive = isActive
    )
}
