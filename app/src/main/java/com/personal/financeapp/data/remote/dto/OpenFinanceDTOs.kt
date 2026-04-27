package com.personal.financeapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AuthRequest(
    @SerializedName("clientId") val clientId: String,
    @SerializedName("clientSecret") val clientSecret: String
)

data class AuthResponse(
    @SerializedName("apiKey") val apiKey: String
)

data class ConnectTokenResponse(
    @SerializedName("accessToken") val accessToken: String? = null,
    @SerializedName("connectToken") val connectToken: String? = null,
    @SerializedName("connectUrl") val connectUrl: String? = null
)

data class ConnectTokenRequest(
    @SerializedName("clientUserId") val clientUserId: String? = null,
    @SerializedName("customerId") val customerId: String? = null,
    @SerializedName("webhookUrl") val webhookUrl: String? = null,
    @SerializedName("options") val options: ConnectTokenOptions? = null
)

data class ConnectTokenOptions(
    @SerializedName("clientName") val clientName: String? = null,
    @SerializedName("productFilters") val productFilters: List<String>? = null,
    @SerializedName("androidRedirect") val androidRedirect: String? = null,
    @SerializedName("oauthRedirectUri") val oauthRedirectUri: String? = null,
    @SerializedName("includeSandbox") val includeSandbox: Boolean? = null
)

data class PluggyTransactionsResponse(
    @SerializedName("results") val results: List<PluggyTransaction>,
    @SerializedName("total") val total: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("page") val page: Int
)

data class PluggyTransaction(
    @SerializedName("id") val id: String,
    @SerializedName("description") val description: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("date") val date: String,
    @SerializedName("category") val category: String?
)

data class PluggyItemResponse(
    @SerializedName("id") val id: String,
    @SerializedName("status") val status: String, // UPDATED, UPDATING, LOGIN_ERROR, OUTDATED, etc.
    @SerializedName("connector") val connector: PluggyConnector? = null,
    @SerializedName("error") val error: PluggyError? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null
)

data class PluggyConnector(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("institutionUrl") val institutionUrl: String? = null,
    @SerializedName("imageUrl") val imageUrl: String? = null
)

data class PluggyError(
    @SerializedName("code") val code: String? = null,
    @SerializedName("message") val message: String? = null
)

data class PluggyAccountsResponse(
    @SerializedName("results") val results: List<PluggyAccount>,
    @SerializedName("total") val total: Int
)

data class PluggyAccount(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String, // BANK, CREDIT
    @SerializedName("balance") val balance: Double? = null,
    @SerializedName("number") val number: String? = null
)

