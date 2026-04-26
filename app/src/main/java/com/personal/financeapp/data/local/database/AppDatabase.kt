package com.personal.financeapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.personal.financeapp.data.local.dao.*
import com.personal.financeapp.data.local.entity.*

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        SubcategoryEntity::class,
        GoalEntity::class,
        ExchangeRateEntity::class,
        VehicleAlertEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun goalDao(): GoalDao
    abstract fun exchangeRateDao(): ExchangeRateDao
    abstract fun vehicleAlertDao(): VehicleAlertDao
    
    companion object {
        const val DATABASE_NAME = "finance_app.db"
    }
}
