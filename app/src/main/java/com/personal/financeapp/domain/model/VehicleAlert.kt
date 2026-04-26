package com.personal.financeapp.domain.model

data class VehicleAlert(
    val id: Long = 0,
    val alertType: String,
    val lastKm: Int? = null,
    val lastDate: String? = null,
    val nextKm: Int? = null,
    val nextDate: String? = null,
    val isDismissed: Boolean = false
)
