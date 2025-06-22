package com.example.backendtest.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.backendtest.SharedPreferencesManager
import com.example.backendtest.data.network.ApiService

class LoginViewModelFactory(
    private val api: ApiService,
    private val preferencesManager: SharedPreferencesManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(api, preferencesManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
