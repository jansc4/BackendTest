package com.example.backendtest.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://10.0.2.2:8000/" //10.0.2.2

    // Interceptor do logowania requestów HTTP (pomocne przy debugowaniu)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Interceptor do dodawania tokenu autoryzacji
    private val authInterceptor = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .apply {
                    UserSession.token?.let {
                        addHeader("Authorization", "Bearer $it")
                    }
                }
                .build()
            chain.proceed(request)
        }
        .addInterceptor(loggingInterceptor) // dodajemy logging
        .build()

    // Budowanie instancji Retrofit
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(authInterceptor)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Tworzenie instancji API
    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
