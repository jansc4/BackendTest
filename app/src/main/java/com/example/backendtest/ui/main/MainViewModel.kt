package com.example.backendtest.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.backendtest.SharedPreferencesManager
import com.example.backendtest.StepSensorManager
import com.example.backendtest.data.network.ApiService
import com.example.backendtest.data.network.StepHistoryEntry
import com.example.backendtest.data.network.UpdateStepsGoalRequest
import com.example.backendtest.data.network.UpdateStepsRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
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

    private val _dailyGoal = MutableStateFlow(preferencesManager.getDailyGoal())
    val dailyGoal: StateFlow<Int> = _dailyGoal.asStateFlow()


    private val _stepsHistory = MutableStateFlow<List<StepHistoryEntry>>(emptyList())
    val stepsHistory: StateFlow<List<StepHistoryEntry>> = _stepsHistory.asStateFlow()

    private val _stepsHistoryState = MutableStateFlow<StepsHistoryState>(StepsHistoryState.Loading)
    val stepsHistoryState: StateFlow<StepsHistoryState> = _stepsHistoryState.asStateFlow()


    private var initialStepsFromApi: Int = 0

    private var pendingSteps: Int? = null

    init {
        loadDailySteps()
        observeSteps()
        startPeriodicUpdateJob()
        loadStepsHistory()
    }
    private fun loadDailySteps() {
        viewModelScope.launch {
            _stepsState.value = StepsState.Loading
            try {
                val response = api.getDailySteps()
                initialStepsFromApi = response.steps ?: 0
                _dailySteps.value = initialStepsFromApi

                val serverGoal = response.maxSteps ?: 10000
                _dailyGoal.value = serverGoal
                preferencesManager.saveDailyGoal(serverGoal) // synchronizacja lokalna z serwerem

                _stepsState.value = StepsState.Success
            } catch (e: Exception) {
                _stepsState.value = StepsState.Error("Nie można załadować kroków: ${e.message}")
            }
        }
    }


    private fun startPeriodicUpdateJob() {
        viewModelScope.launch {
            while (true) {
                delay(5000) // aktualizacja co 5 sekund

                pendingSteps?.let { steps ->
                    try {
                        val response = api.updateSteps(UpdateStepsRequest(steps))
                        response.maxSteps?.let {
                            _dailyGoal.value = it
                        }
                        Log.d("MainViewModel", "Zaktualizowano kroki: $steps")
                        pendingSteps = null // wyczyść po sukcesie
                    } catch (e: Exception) {
                        Log.e("MainViewModel", "Błąd aktualizacji kroków", e)
                    }
                }
            }
        }
    }

    private fun observeSteps() {
        viewModelScope.launch {
            stepSensorManager.steps.collect { additionalSteps ->
                val totalSteps = initialStepsFromApi + additionalSteps
                if (totalSteps > _dailySteps.value) {
                    _dailySteps.value = totalSteps
                    pendingSteps = totalSteps
                }
            }
        }
    }

    fun loadStepsHistory() {
        viewModelScope.launch {
            _stepsHistoryState.value = StepsHistoryState.Loading
            try {
                val history = api.getStepsHistory()
                val sorted = history.sortedBy { it.date }.takeLast(7) // tylko 7 ostatnich dni
                _stepsHistoryState.value = StepsHistoryState.Success(sorted)
            } catch (e: Exception) {
                _stepsHistoryState.value = StepsHistoryState.Error("Nie udało się pobrać historii")
            }
        }
    }

    fun setDailyGoal(goal: Int) {
        viewModelScope.launch {
            try {
                val response = api.updateStepsGoal(UpdateStepsGoalRequest(goal))
                response.maxSteps?.let { serverGoal ->
                    _dailyGoal.value = serverGoal
                    preferencesManager.saveDailyGoal(serverGoal)
                    Log.d("MainViewModel", "Cel kroków zaktualizowany do $serverGoal na serwerze i lokalnie")
                } ?: run {
                    // Jeśli serwer nie zwróci celu, zachowaj lokalną wartość
                    _dailyGoal.value = goal
                    preferencesManager.saveDailyGoal(goal)
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Błąd aktualizacji celu kroków na serwerze", e)
                // Nadal aktualizujemy lokalnie, aby użytkownik widział efekt zmiany
                _dailyGoal.value = goal
                preferencesManager.saveDailyGoal(goal)
            }
        }
    }


}


sealed class StepsState {
    object Loading : StepsState()
    object Success : StepsState()
    data class Error(val message: String) : StepsState()
}

sealed class StepsHistoryState {
    object Loading : StepsHistoryState()
    data class Success(val history: List<StepHistoryEntry>) : StepsHistoryState()
    data class Error(val message: String) : StepsHistoryState()
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