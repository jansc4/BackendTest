package com.example.backendtest.data.network

import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("/register")
    suspend fun register(@Body body: RegisterRequest): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("grant_type") grantType: String = "password",
        @Field("scope") scope: String = "",
        @Field("client_id") clientId: String = "",
        @Field("client_secret") clientSecret: String = ""
    ): LoginResponse



    @GET("/me")
    suspend fun getMe(@Header("Authorization") authHeader: String): MeResponse

    @GET("steps/daily")
    suspend fun getDailySteps(): DailyStepsResponse

    @POST("steps/update")
    suspend fun updateSteps(@Body request: UpdateStepsRequest): UpdateStepsResponse

}