package com.personal.financeapp.domain.usecase

import com.personal.financeapp.domain.model.Goal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class AnalyzedGoal(
    val rawGoal: Goal,
    val progressPercent: Int,
    val remainingMonths: Long,
    val expectedEndAmount: Double,
    val isShortfall: Boolean,
    val totalShortfall: Double,
    val recommendedExtraMonthly: Double
)

class AnalyzeGoalUseCase @Inject constructor() {
    
    operator fun invoke(goals: List<Goal>): List<AnalyzedGoal> {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val today = LocalDate.now()
        
        return goals.map { goal ->
            val targetDate = try {
                LocalDate.parse(goal.targetDate, formatter)
            } catch (e: Exception) {
                today.plusMonths(12) // Fallback
            }
            
            var remainingMonths = ChronoUnit.MONTHS.between(today, targetDate)
            if (remainingMonths <= 0) remainingMonths = 1
            
            val progressPercent = if (goal.targetAmount > 0) {
                ((goal.currentAmount / goal.targetAmount) * 100).toInt().coerceIn(0, 100)
            } else 0
            
            val expectedEndAmount = goal.currentAmount + (goal.monthlyContribution * remainingMonths)
            
            val totalShortfall = goal.targetAmount - expectedEndAmount
            val isShortfall = totalShortfall > 0
            
            val recommendedExtraMonthly = if (isShortfall) {
                totalShortfall / remainingMonths
            } else {
                0.0
            }

            AnalyzedGoal(
                rawGoal = goal,
                progressPercent = progressPercent,
                remainingMonths = remainingMonths,
                expectedEndAmount = expectedEndAmount,
                isShortfall = isShortfall,
                totalShortfall = if (isShortfall) totalShortfall else 0.0,
                recommendedExtraMonthly = recommendedExtraMonthly
            )
        }
    }
}
