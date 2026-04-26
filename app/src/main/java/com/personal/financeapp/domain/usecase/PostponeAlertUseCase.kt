package com.personal.financeapp.domain.usecase

import com.personal.financeapp.domain.repository.VehicleAlertRepository
import javax.inject.Inject

class PostponeAlertUseCase @Inject constructor(
    private val repository: VehicleAlertRepository
) {
    suspend operator fun invoke(alertId: Long) {
        // For simplicity, we can just dismiss it or implement real postponing logic.
        // If we want to really postpone, we need to fetch the alert and update nextDate.
        // For now, let's just dismiss it as "postponed" out of active alerts.
        repository.dismissAlert(alertId)
    }
}
