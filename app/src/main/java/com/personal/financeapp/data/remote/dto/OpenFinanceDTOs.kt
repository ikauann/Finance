package com.personal.financeapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ConnectTokenResponse(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("connectUrl") val connectUrl: String? = null
)

data class ConnectTokenRequest(
    @SerializedName("customerId") val customerId: String? = null,
    @SerializedName("webhookUrl") val webhookUrl: String? = null,
    @SerializedName("options") val options: ConnectTokenOptions? = null
)

data class ConnectTokenOptions(
    @SerializedName("clientName") val clientName: String? = null,
    @SerializedName("productFilters") val productFilters: List<String>? = null,
    @SerializedName("androidRedirect") val androidRedirect: String? = null
)

data class PluggyTransaction(
    @SerializedName("id") val id: String,
    @SerializedName("description") val description: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("date") val date: String,
    @SerializedName("category") val category: String?
)
