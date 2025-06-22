package com.example.backendtest.data.network

import android.content.Context
import android.content.SharedPreferences

object UserSession{
    private lateinit var appContext:Context
    private const val PREF_NAME = "auth"
    private const val KEY_AUTH_TOKEN = "token"
    private const val KEY_AUTH_REFRESCH_TOKEN = "refresh_token"
    //    var user: UserResponse? = null
    var token: String? = null
    var refresh_token: String? = null

    fun init(context: Context){
        appContext = context.applicationContext
    }

    fun saveAuthToken(access_token: String,  refresh_token: String){
        val sharedPreferences: SharedPreferences = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(KEY_AUTH_TOKEN, access_token).apply()
        sharedPreferences.edit().putString(KEY_AUTH_REFRESCH_TOKEN, refresh_token).apply()
        this.token = access_token
        this.refresh_token = refresh_token
    }

    fun getAuthToken() {
        val sharedPreferences: SharedPreferences = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        token = sharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }

    fun getAuthRefreshToken() {
        val sharedPreferences: SharedPreferences = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        refresh_token = sharedPreferences.getString(KEY_AUTH_REFRESCH_TOKEN, null)
    }

    fun clearAuthToken() {
        val sharedPreferences: SharedPreferences = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(KEY_AUTH_TOKEN).apply()

        token = null
    }
    fun clearAuthRefreshToken() {
        val sharedPreferences: SharedPreferences = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().remove(KEY_AUTH_REFRESCH_TOKEN).apply()

        refresh_token = null
    }
}