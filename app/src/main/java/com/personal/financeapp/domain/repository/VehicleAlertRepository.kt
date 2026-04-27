package com.personal.financeapp.domain.repository

import com.personal.financeapp.domain.model.VehicleAlert
import kotlinx.coroutines.flow.Flow

interface VehicleAlertRepository {
    fun getActiveAlerts(): Flow<List<VehicleAlert>>
    suspend fun insertAlert(alert: VehicleAlert): Long
    suspend fun updateAlert(alert: VehicleAlert)
    suspend fun dismissAlert(alertId: Long)
    suspend fun resetAllData()
}
