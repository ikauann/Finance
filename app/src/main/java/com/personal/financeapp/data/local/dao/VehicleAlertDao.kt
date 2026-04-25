package com.personal.financeapp.data.local.dao

import androidx.room.*
import com.personal.financeapp.data.local.entity.VehicleAlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleAlertDao {
    @Query("SELECT * FROM vehicle_alerts WHERE is_dismissed = 0 ORDER BY next_date")
    fun getActiveAlerts(): Flow<List<VehicleAlertEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: VehicleAlertEntity): Long
    
    @Update
    suspend fun updateAlert(alert: VehicleAlertEntity)
    
    @Query("UPDATE vehicle_alerts SET is_dismissed = 1 WHERE id = :alertId")
    suspend fun dismissAlert(alertId: Long)
}
