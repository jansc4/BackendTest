package com.example.backendtest.data.network

import com.example.backendtest.data.model.Exercise
import com.example.backendtest.data.model.ExercisePerformance
import com.example.backendtest.data.model.ExercisePerformanceData
import com.example.backendtest.data.model.Steps
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDate

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

    //exercise
    @GET("/exercises")
    suspend fun getExercisesList(@Header("Authorization") authHeader: String):List<Exercise>

    @GET("exercise/{id}")
    suspend fun getExerciseById(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String
    ): Exercise

    @POST("exercise")
    suspend fun addExercise(@Header("Authorization") authHeader: String, @Body body: NewExerciseRequest):Exercise

    @PUT("exercise/{id}")
    suspend fun updateExercise(@Header("Authorization") authHeader: String, @Body body: ExerciseRequest, @Path("id") id: String):Exercise

    @DELETE("exercise/{id}")
    suspend fun deleteExercise(@Header("Authorization") authHeader: String, @Path("id") id: String):Response<Void>


    //calendar
    @GET("calendar")
    suspend fun getAllCalendarEntries(@Header("Authorization") authHeader: String):List<CalendarDayResponse>

    @GET("calendar/{id}")
    suspend fun getCalendarEntryById(@Header("Authorization") authHeader: String, @Path("id") id: String):List<CalendarDayResponse>

    @GET("calendar/date/{date}")
    suspend fun getCalendarEntryByDate(
        @Header("Authorization") authHeader: String,
        @Path("date") date: LocalDate
    ): List<CalendarDayResponse>

    @POST("calendar")
    suspend fun addCalendarDay(
        @Header("Authorization") authHeader: String,
        @Body request: ExercisePerformanceRequest
    ): CalendarDayResponse

    @PUT("calendar/{calendar_id}")
    suspend fun updateCalendarById(@Header("Authorization") authHeader: String, @Body body: ExercisePerformanceRequest, @Path("calendar_id") id: String):CalendarDayResponse

    @DELETE("calendar/{id}")
    suspend fun deleteCalendarEntryById(@Header("Authorization") authHeader: String, @Path("id") id: String):Result<Void>

    //calendar/exercisePerformance
    @PUT("/calendar/{calendar_id}/exercise/{exercise_id}")
    suspend fun updateCalendarEntryByCalendarId(@Header("Authorization") authHeader: String, @Body body: ExercisePerformanceData, @Path("calendar_id") id: String, @Path("exercise_id") exerciseId: String):ExercisePerformance

    @POST("/calendar/{calendar_id}/exercise")
    suspend fun addCalendarEntryByCalendarId(@Header("Authorization") authHeader: String, @Body body: ExercisePerformanceData, @Path("calendar_id") id: String):ExercisePerformance

    @DELETE("/calendar/{calendar_id}/exercise/{exercise_id}")
    suspend fun addCalendarEntryByCalendarId(@Header("Authorization") authHeader: String, @Path("calendar_id") id: String, @Path("exercise_id") exerciseId: String):Result<Void>

    //steps
    @GET("/steps/today")
    suspend fun getStepsForToday(@Header("Authorization") authHeader: String):Steps

    @PUT("/steps/today")
    suspend fun getStepsForToday(@Header("Authorization") authHeader: String, @Body body: Steps,):Steps

}