package com.personal.financeapp.presentation.screens.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personal.financeapp.domain.model.Goal
import com.personal.financeapp.domain.repository.GoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.personal.financeapp.domain.usecase.AnalyzeGoalUseCase

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val repository: GoalRepository,
    private val analyzeGoalUseCase: AnalyzeGoalUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalsUiState())
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()

    init {
        loadGoals()
    }

    private fun loadGoals() {
        viewModelScope.launch {
            repository.getAllGoals()
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .collect { goals ->
                    val analyzed = analyzeGoalUseCase(goals)
                    _uiState.update { 
                        it.copy(
                            activeGoals = analyzed.filter { a -> a.rawGoal.isActive },
                            completedGoals = analyzed.filter { a -> !a.rawGoal.isActive },
                            isLoading = false
                        ) 
                    }
                }
        }
    }

    fun addGoal(name: String, target: Double) {
        viewModelScope.launch {
            repository.insertGoal(
                Goal(
                    name = name,
                    targetAmount = target,
                    targetCurrency = "BRL",
                    targetDate = "2024-12-31" // Default or selected date
                )
            )
        }
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }
}

data class GoalsUiState(
    val activeGoals: List<com.personal.financeapp.domain.usecase.AnalyzedGoal> = emptyList(),
    val completedGoals: List<com.personal.financeapp.domain.usecase.AnalyzedGoal> = emptyList(),
    val isLoading: Boolean = false
)
