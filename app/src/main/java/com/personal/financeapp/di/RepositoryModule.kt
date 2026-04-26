package com.personal.financeapp.di

import com.personal.financeapp.data.repository.AiRepositoryImpl
import com.personal.financeapp.data.repository.GoalRepositoryImpl
import com.personal.financeapp.data.repository.OpenFinanceRepositoryImpl
import com.personal.financeapp.data.repository.TransactionRepositoryImpl
import com.personal.financeapp.domain.repository.AiRepository
import com.personal.financeapp.domain.repository.GoalRepository
import com.personal.financeapp.domain.repository.OpenFinanceRepository
import com.personal.financeapp.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        transactionRepositoryImpl: TransactionRepositoryImpl
    ): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindAiRepository(
        aiRepositoryImpl: AiRepositoryImpl
    ): AiRepository

    @Binds
    @Singleton
    abstract fun bindGoalRepository(
        goalRepositoryImpl: GoalRepositoryImpl
    ): GoalRepository

    @Binds
    @Singleton
    abstract fun bindOpenFinanceRepository(
        openFinanceRepositoryImpl: OpenFinanceRepositoryImpl
    ): OpenFinanceRepository

    @Binds
    @Singleton
    abstract fun bindVehicleAlertRepository(
        vehicleAlertRepositoryImpl: com.personal.financeapp.data.repository.VehicleAlertRepositoryImpl
    ): com.personal.financeapp.domain.repository.VehicleAlertRepository
}
