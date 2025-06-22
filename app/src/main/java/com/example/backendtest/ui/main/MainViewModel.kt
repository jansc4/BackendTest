package com.example.backendtest.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.backendtest.SharedPreferencesManager
import com.example.backendtest.StepSensorManager
import com.example.backendtest.data.network.ApiService
import com.example.backendtest.data.network.UpdateStepsRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val api: ApiService,
    private val preferencesManager: SharedPreferencesManager,
    private val stepSensorManager: StepSensorManager
) : ViewModel() {

    private val _stepsState = MutableStateFlow<StepsState>(StepsState.Loading)
    val stepsState: StateFlow<StepsState> = _stepsState.asStateFlow()

    private val _dailySteps = MutableStateFlow(0)
    val dailySteps: StateFlow<Int> = _dailySteps.asStateFlow()

    private val _dailyGoal = MutableStateFlow(10000) // domyślny cel
    val dailyGoal: StateFlow<Int> = _dailyGoal.asStateFlow()

    init {
        loadDailySteps()
        observeSteps()
    }

    private fun observeSteps() {
        viewModelScope.launch {
            stepSensorManager.steps.collect { steps ->
                _dailySteps.value = steps
                updateStepsToApi(steps)
            }
        }
    }

    private fun loadDailySteps() {
        viewModelScope.launch {
            _stepsState.value = StepsState.Loading
            try {
                val response = api.getDailySteps()
                _dailySteps.value = response.steps
                _dailyGoal.value = response.goal
                _stepsState.value = StepsState.Success
            } catch (e: Exception) {
                _stepsState.value = StepsState.Error("Nie można załadować kroków: ${e.message}")
            }
        }
    }

    private var lastUpdateJob: Job? = null
    private fun updateStepsToApi(steps: Int) {
        // Anuluj poprzednie zadanie aktualizacji, jeśli istnieje
        lastUpdateJob?.cancel()

        // Utwórz nowe zadanie aktualizacji
        lastUpdateJob = viewModelScope.launch {
            try {
                // opóźnienie aby nie wysyłać zbyt wielu requestów
                delay(5000)

                val request = UpdateStepsRequest(steps = steps)
                api.updateSteps(request)
            } catch (e: Exception) {
                // Obsługa błędu aktualizacji
                _stepsState.value = StepsState.Error("Błąd aktualizacji kroków")
            }
        }
    }
}

sealed class StepsState {
    object Loading : StepsState()
    object Success : StepsState()
    data class Error(val message: String) : StepsState()
}

// Factory dla MainViewModel
class MainViewModelFactory(
    private val api: ApiService,
    private val preferencesManager: SharedPreferencesManager,
    private val stepSensorManager: StepSensorManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(api, preferencesManager, stepSensorManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}