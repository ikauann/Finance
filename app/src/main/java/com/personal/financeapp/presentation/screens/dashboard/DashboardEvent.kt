package com.personal.financeapp.presentation.screens.dashboard

sealed class DashboardEvent {
    object SeedDatabase : DashboardEvent()
    data class AddTransaction(val transaction: com.personal.financeapp.domain.model.Transaction) : DashboardEvent()
}
