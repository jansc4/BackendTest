package com.example.backendtest.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.backendtest.SharedPreferencesManager
import com.example.backendtest.data.network.ApiService
import com.example.backendtest.data.network.RegisterRequest
import com.example.backendtest.data.network.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val api: ApiService,
    private val preferencesManager: SharedPreferencesManager
) : ViewModel() {

    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val signUpState: StateFlow<SignUpState> = _signUpState.asStateFlow()

    // Walidacja w czasie rzeczywistym
    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    fun validateEmail(email: String) {
        _emailError.value = when {
            email.isEmpty() -> "Email nie może być pusty"
            !email.contains("@") -> "Nieprawidłowy format email"
            else -> null
        }
    }

    fun validatePassword(password: String) {
        _passwordError.value = when {
            password.length < 8 -> "Hasło musi mieć minimum 8 znaków"
            !password.any { it.isDigit() } -> "Hasło musi zawierać cyfry"
            !password.any { it.isUpperCase() } -> "Hasło musi zawierać wielkie litery"
            else -> null
        }
    }

    fun signUp(username: String, email: String, password: String) {
        viewModelScope.launch {
            _signUpState.value = SignUpState.Loading

            try {
                val request = RegisterRequest(
                    username = username,
                    email = email,
                    password = password
                )

                val response = api.register(request)

                // Po udanej rejestracji automatycznie logujemy
                val loginResponse = api.login(
                    username = email,
                    password = password
                    // pozostałe parametry mają wartości domyślne
                )

                UserSession.saveAuthToken(
                    loginResponse.access_token,
                    loginResponse.refresh_token
                )

                _signUpState.value = SignUpState.Success
            } catch (e: Exception) {
                _signUpState.value = SignUpState.Error(e.message ?: "Błąd rejestracji")
            }
        }
    }
}

sealed class SignUpState {
    object Idle : SignUpState()
    object Loading : SignUpState()
    object Success : SignUpState()
    data class Error(val message: String) : SignUpState()
}

// SignUpViewModelFactory.kt
class SignUpViewModelFactory(
    private val api: ApiService,
    private val preferencesManager: SharedPreferencesManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignUpViewModel(api, preferencesManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
