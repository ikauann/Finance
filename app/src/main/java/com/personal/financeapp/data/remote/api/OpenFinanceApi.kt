package com.personal.financeapp.data.remote.api

import com.personal.financeapp.data.remote.dto.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenFinanceApi {
    @POST("auth")
    suspend fun authenticate(
        @Body request: AuthRequest
    ): AuthResponse

    @POST("connect_token")
    suspend fun createConnectToken(
        @Header("X-API-KEY") apiKey: String,
        @Body request: ConnectTokenRequest = ConnectTokenRequest()
    ): ConnectTokenResponse

    @GET("items/{itemId}")
    suspend fun getItem(
        @Header("X-API-KEY") apiKey: String,
        @Path("itemId") itemId: String
    ): PluggyItemResponse

    @GET("accounts")
    suspend fun getAccounts(
        @Header("X-API-KEY") apiKey: String,
        @Query("itemId") itemId: String
    ): PluggyAccountsResponse

    @GET("transactions")
    suspend fun getTransactions(
        @Header("X-API-KEY") apiKey: String,
        @Query("accountId") accountId: String
    ): PluggyTransactionsResponse
}
