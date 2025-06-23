package com.example.backendtest.data.network

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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

    @POST("/refresh")
    suspend fun refreshToken(
        @Body refreshToken: String
    ): LoginResponse



    @GET("/me")
    suspend fun getMe(@Header("Authorization") authHeader: String): MeResponse

    @GET("steps/today")
    suspend fun getDailySteps(): DailyStepsResponse

    @PUT("steps/today")
    suspend fun updateSteps(@Body request: UpdateStepsRequest): UpdateStepsResponse

    @PUT("steps/today/goal")
    suspend fun updateStepsGoal(@Body request: UpdateStepsGoalRequest): UpdateStepsResponse

    @GET("steps/history")
    suspend fun getStepsHistory(): List<StepHistoryEntry>

    @GET("calendar")
    suspend fun getAllCalendarEntries(): List<CalendarEntry>

    @GET("calendar/{calendarId}")
    suspend fun getCalendarEntry(@Path("calendarId") calendarId: String): CalendarEntry

    @POST("calendar/{calendarId}/exercise")
    suspend fun addExerciseToCalendar(
        @Path("calendarId") calendarId: String,
        @Body exercise: ExercisePerformance
    ): ExercisePerformance

    @PUT("calendar/{calendarId}/exercise/{exerciseId}")
    suspend fun updateExerciseInCalendar(
        @Path("calendarId") calendarId: String,
        @Path("exerciseId") exerciseId: String,
        @Body exercise: ExercisePerformance
    ): ExercisePerformance

    @DELETE("calendar/{calendarId}/exercise/{exerciseId}")
    suspend fun deleteExerciseFromCalendar(
        @Path("calendarId") calendarId: String,
        @Path("exerciseId") exerciseId: String
    )


}