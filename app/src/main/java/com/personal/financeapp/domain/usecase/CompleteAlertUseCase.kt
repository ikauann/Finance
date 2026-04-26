package com.personal.financeapp.domain.usecase

import com.personal.financeapp.domain.repository.VehicleAlertRepository
import javax.inject.Inject

class CompleteAlertUseCase @Inject constructor(
    private val repository: VehicleAlertRepository
) {
    suspend operator fun invoke(alertId: Long) {
        repository.dismissAlert(alertId)
    }
}
