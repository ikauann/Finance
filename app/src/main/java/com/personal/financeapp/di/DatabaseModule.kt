package com.personal.financeapp.di

import android.content.Context
import androidx.room.Room
import com.personal.financeapp.data.local.dao.*
import com.personal.financeapp.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // REMOVER em produção
            .build()
    }
    
    @Provides
    fun provideTransactionDao(database: AppDatabase): TransactionDao {
        return database.transactionDao()
    }
    
    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }
    
    @Provides
    fun provideGoalDao(database: AppDatabase): GoalDao {
        return database.goalDao()
    }
    
    @Provides
    fun provideExchangeRateDao(database: AppDatabase): ExchangeRateDao {
        return database.exchangeRateDao()
    }
    
    @Provides
    fun provideVehicleAlertDao(database: AppDatabase): VehicleAlertDao {
        return database.vehicleAlertDao()
    }

    @Provides
    fun provideConnectedItemDao(database: AppDatabase): ConnectedItemDao {
        return database.connectedItemDao()
    }
}
