package com.personal.financeapp.domain.usecase

import com.personal.financeapp.domain.model.Category
import com.personal.financeapp.domain.model.Goal
import com.personal.financeapp.domain.model.VehicleAlert
import com.personal.financeapp.domain.repository.GoalRepository
import com.personal.financeapp.domain.repository.TransactionRepository
import com.personal.financeapp.domain.repository.VehicleAlertRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SeedDatabaseUseCase @Inject constructor(
    private val repository: TransactionRepository,
    private val alertRepository: VehicleAlertRepository,
    private val goalRepository: GoalRepository
) {
    suspend operator fun invoke() {
        val categories = repository.getAllCategories().first()
        if (categories.isEmpty()) {
            val defaultCategories = listOf(
                Category(1, "Alimentação", "restaurant", "#FF9800"),
                Category(2, "Transporte", "directions_car", "#2196F3"),
                Category(3, "Moradia", "home", "#E91E63"),
                Category(4, "Saúde", "medical_services", "#4CAF50"),
                Category(5, "Lazer", "sports_esports", "#9C27B0"),
                Category(6, "Salário", "payments", "#009688"),
                Category(7, "Educação", "school", "#3F51B5"),
                Category(8, "Outros", "more_horiz", "#607D8B")
            )
            repository.insertCategories(defaultCategories)
        }

        val alerts = alertRepository.getActiveAlerts().first()
        if (alerts.isEmpty()) {
            val testAlert = VehicleAlert(
                alertType = "Troca de óleo JAC J2",
                nextKm = 250,
                nextDate = "15 dias"
            )
            alertRepository.insertAlert(testAlert)
        }
        
        val goals = goalRepository.getAllGoals().first()
        if (goals.isEmpty()) {
            val fundoSuica = Goal(
                name = "Fundo Suíça",
                targetAmount = 50000.0,
                targetCurrency = "CHF",
                targetDate = "31/03/2027",
                currentAmount = 1967.0,
                priority = "Alta",
                monthlyContribution = 26634.0,
                purpose = "Compra de Imóvel",
                exchangeRate = 6.10,
                icon = "🇨🇭",
                color = "#E53935",
                historicalContributions = listOf(2000.0, 3000.0, 4000.0, 3500.0, 5000.0, 2500.0),
                isShortfall = true,
                shortfallAmount = 8500.0,
                shortfallRecommendation = "Considere aumentar seu aporte em R\$ 450 nos próximos 3 meses para manter o cronograma original."
            )
            val evolucaoPleno = Goal(
                name = "Evolução Pleno (Set/2026)",
                targetAmount = 100.0,
                targetCurrency = "BRL",
                targetDate = "01/09/2026",
                currentAmount = 30.0,
                priority = "Média",
                purpose = "Simular impacto de promoção",
                icon = "💼",
                color = "#D2B48C"
            )
            val fundoEmergencia = Goal(
                name = "Fundo Emergência",
                targetAmount = 10000.0,
                targetCurrency = "BRL",
                targetDate = "15/01/2026",
                currentAmount = 10000.0,
                isActive = false,
                icon = "✅",
                color = "#9E9E9E"
            )
            goalRepository.insertGoal(fundoSuica)
            goalRepository.insertGoal(evolucaoPleno)
            goalRepository.insertGoal(fundoEmergencia)
        }
    }
}
