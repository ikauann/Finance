package com.personal.financeapp.presentation.screens.dashboard

sealed class DashboardEvent {
    object SeedDatabase : DashboardEvent()
    data class AddTransaction(val transaction: com.personal.financeapp.domain.model.Transaction) : DashboardEvent()
    data class CompleteAlert(val alertId: Long) : DashboardEvent()
    data class PostponeAlert(val alertId: Long) : DashboardEvent()
    data class AddAlert(val alert: com.personal.financeapp.domain.model.VehicleAlert) : DashboardEvent()
}
