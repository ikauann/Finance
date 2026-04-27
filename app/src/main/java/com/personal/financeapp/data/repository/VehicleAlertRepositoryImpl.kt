package com.personal.financeapp.data.repository

import com.personal.financeapp.data.local.dao.VehicleAlertDao
import com.personal.financeapp.data.local.entity.VehicleAlertEntity
import com.personal.financeapp.domain.model.VehicleAlert
import com.personal.financeapp.domain.repository.VehicleAlertRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class VehicleAlertRepositoryImpl @Inject constructor(
    private val dao: VehicleAlertDao
) : VehicleAlertRepository {

    override fun getActiveAlerts(): Flow<List<VehicleAlert>> {
        return dao.getActiveAlerts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertAlert(alert: VehicleAlert): Long {
        return dao.insertAlert(alert.toEntity())
    }

    override suspend fun updateAlert(alert: VehicleAlert) {
        dao.updateAlert(alert.toEntity())
    }

    override suspend fun dismissAlert(alertId: Long) {
        dao.dismissAlert(alertId)
    }

    override suspend fun resetAllData() {
        dao.deleteAllAlerts()
    }
}

fun VehicleAlertEntity.toDomain() = VehicleAlert(
    id = id,
    alertType = alertType,
    lastKm = lastKm,
    lastDate = lastDate,
    nextKm = nextKm,
    nextDate = nextDate,
    isDismissed = isDismissed
)

fun VehicleAlert.toEntity() = VehicleAlertEntity(
    id = id,
    alertType = alertType,
    lastKm = lastKm,
    lastDate = lastDate,
    nextKm = nextKm,
    nextDate = nextDate,
    isDismissed = isDismissed
)
