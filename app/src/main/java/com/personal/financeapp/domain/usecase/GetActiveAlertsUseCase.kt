package com.personal.financeapp.domain.usecase

import com.personal.financeapp.domain.model.VehicleAlert
import com.personal.financeapp.domain.repository.VehicleAlertRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetActiveAlertsUseCase @Inject constructor(
    private val repository: VehicleAlertRepository
) {
    operator fun invoke(): Flow<List<VehicleAlert>> {
        return repository.getActiveAlerts()
    }
}
