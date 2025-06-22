package com.example.backendtest.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.backendtest.SharedPreferencesManager
import com.example.backendtest.data.UserPreferences
import com.example.backendtest.data.network.ApiService
import com.example.backendtest.data.network.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// LoginViewModel.kt
class LoginViewModel(
    private val api: ApiService,
    private val preferencesManager: SharedPreferencesManager
) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun login(email: String, password: String, rememberMe: Boolean) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val credentials = mapOf(
                    "email" to email,
                    "password" to password
                )
                val response = api.login(credentials)
                UserSession.saveAuthToken(response.access_token, response.refresh_token)

                if (rememberMe) {
                    preferencesManager.saveUserPreferences(
                        UserPreferences(
                            email = email,
                            password = password,
                            dailyStepsGoal = 10000,
                            isRememberMeEnabled = true
                        )
                    )
                }
                _loginState.value = LoginState.Success
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}