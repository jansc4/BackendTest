package com.example.backendtest.data.network

data class RegisterRequest(val username: String, val email: String, val password: String)
data class RegisterResponse(val username: String, val email: String)

data class LoginResponse(val access_token: String, val refresh_token: String, val token_type: String)
data class MeResponse(val username: String, val email: String)