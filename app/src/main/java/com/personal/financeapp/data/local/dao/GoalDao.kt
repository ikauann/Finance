package com.personal.financeapp.data.local.dao

import androidx.room.*
import com.personal.financeapp.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE is_active = 1 ORDER BY target_date")
    fun getActiveGoals(): Flow<List<GoalEntity>>
    
    @Query("SELECT * FROM goals WHERE id = :id")
    suspend fun getGoalById(id: Long): GoalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity): Long

    @Update
    suspend fun updateGoal(goal: GoalEntity)

    @Delete
    suspend fun deleteGoal(goal: GoalEntity)

    @Query("UPDATE goals SET current_amount = current_amount + :amount WHERE id = :goalId")
    suspend fun addValueToGoal(goalId: Long, amount: Double)
}
