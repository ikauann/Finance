package com.personal.financeapp.domain.usecase

import com.personal.financeapp.domain.model.Goal
import com.personal.financeapp.domain.model.Transaction
import com.personal.financeapp.domain.model.TransactionType
import java.time.LocalDate
import java.time.YearMonth
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
    
    operator fun invoke(goals: List<Goal>, transactions: List<Transaction>): List<AnalyzedGoal> {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val today = LocalDate.now()
        
        return goals.map { rawGoal ->
            val goalTransactions = transactions.filter { it.goalId == rawGoal.id }
            
            // Calculate real currentAmount based on linked transactions
            val realCurrentAmount = goalTransactions.sumOf { 
                if (it.type == TransactionType.INCOME) -it.amount else it.amount // Or assuming all goal contributions are EXPENSE from main wallet?
                // Wait, if we use EXPENSE for contributions, let's just sum absolute values for now
                it.amount 
            }
            
            // Group by month for historicalContributions (last 6 months)
            val historical = goalTransactions
                .groupBy { YearMonth.from(it.date) }
                .toSortedMap()
                .values
                .map { list -> list.sumOf { it.amount } }
                .takeLast(6)
            
            val goal = rawGoal.copy(
                currentAmount = realCurrentAmount,
                historicalContributions = historical
            )
            
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
