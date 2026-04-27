package com.personal.financeapp.domain.usecase

import com.personal.financeapp.domain.repository.GoalRepository
import com.personal.financeapp.domain.repository.TransactionRepository
import com.personal.financeapp.domain.repository.VehicleAlertRepository
import javax.inject.Inject

class SeedDatabaseUseCase @Inject constructor(
    private val repository: TransactionRepository,
    private val alertRepository: VehicleAlertRepository,
    private val goalRepository: GoalRepository
) {
    suspend fun reset() {
        repository.resetAllData()
        goalRepository.resetAllData()
        alertRepository.resetAllData()
    }

    suspend operator fun invoke() {
        // Desativado para manter o app totalmente vazio conforme solicitação do usuário
    }
}
