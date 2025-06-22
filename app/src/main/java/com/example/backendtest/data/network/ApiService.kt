package com.example.backendtest.data.network

import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("/register")
    suspend fun register(@Body body: RegisterRequest): RegisterResponse

    @FormUrlEncoded
    @POST("/login")
    suspend fun login(
        @FieldMap credentials: Map<String, String>
    ): LoginResponse

    @GET("/me")
    suspend fun getMe(@Header("Authorization") authHeader: String): MeResponse
}