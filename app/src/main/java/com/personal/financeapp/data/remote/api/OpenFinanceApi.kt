package com.personal.financeapp.data.remote.api

import com.personal.financeapp.data.remote.dto.ConnectTokenRequest
import com.personal.financeapp.data.remote.dto.ConnectTokenResponse
import com.personal.financeapp.data.remote.dto.PluggyTransaction
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface OpenFinanceApi {
    @POST("connect_token")
    suspend fun createConnectToken(
        @Header("X-API-KEY") apiKey: String,
        @Body request: ConnectTokenRequest = ConnectTokenRequest()
    ): ConnectTokenResponse

    @GET("transactions")
    suspend fun getTransactions(
        @Header("X-API-KEY") apiKey: String,
        @Query("itemId") itemId: String
    ): List<PluggyTransaction>
}
