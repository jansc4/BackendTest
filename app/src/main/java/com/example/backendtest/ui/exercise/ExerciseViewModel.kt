package com.example.backendtest.ui.exercise

import com.example.backendtest.data.model.Exercise
import com.example.backendtest.data.model.ExerciseData
import com.example.backendtest.data.model.ExerciseType
import com.example.backendtest.data.network.ApiService
import com.example.backendtest.data.network.RetrofitInstance
import com.example.backendtest.data.network.UserSession

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ExerciseViewModel(
    private val api: ApiService = RetrofitInstance.api
) : ViewModel() {

    private val _exerciseState = MutableStateFlow<ExerciseState>(ExerciseState.Idle)
    val exerciseState: StateFlow<ExerciseState> = _exerciseState.asStateFlow()

    private val _selectedExerciseType = MutableStateFlow<ExerciseType?>(null)
    val selectedExerciseType: StateFlow<ExerciseType?> = _selectedExerciseType.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _exerciseList = MutableStateFlow<List<Exercise>>(emptyList())
    val exerciseList: StateFlow<List<Exercise>> = _exerciseList.asStateFlow()

    val filteredExerciseList: StateFlow<List<Exercise>> = combine(
        _exerciseList,
        _selectedExerciseType,
        _searchText
    ) { exercises, selectedType, search ->
        exercises.filter { exercise ->
            (selectedType == null || exercise.exerciseType == selectedType) &&
                    (search.isBlank() ||
                            exercise.name.contains(search, ignoreCase = true) ||
                            exercise.description.contains(search, ignoreCase = true))
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        fetchExercises()
    }

    fun fetchExercises() {
        viewModelScope.launch {
            _exerciseState.value = ExerciseState.Loading
            try {
                val token = UserSession.token ?: throw Exception("Token is null")
                val exercises = api.getExercisesList("Bearer $token")
                _exerciseList.value = exercises
                _exerciseState.value = ExerciseState.Success
            } catch (e: Exception) {
                _exerciseState.value = ExerciseState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun addOrUpdate(exercise: ExerciseData) {
        if (exercise.id != null) {
            updateExercise(exercise.id, exercise)
        } else {
            addExercise(exercise)
        }
    }

    private fun addExercise(exercise: ExerciseData) {
        viewModelScope.launch {
            _exerciseState.value = ExerciseState.Loading
            try {
                val token = UserSession.token ?: throw Exception("Token is null")
                api.addExercise("Bearer $token", exercise.toNewExerciseRequest())
                fetchExercises()
            } catch (e: Exception) {
                _exerciseState.value = ExerciseState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun updateExercise(id: String, exercise: ExerciseData) {
        viewModelScope.launch {
            _exerciseState.value = ExerciseState.Loading
            try {
                val token = UserSession.token ?: throw Exception("Token is null")
                api.updateExercise("Bearer $token", exercise.toExerciseRequest(), id)
                fetchExercises()
            } catch (e: Exception) {
                _exerciseState.value = ExerciseState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun deleteExercise(id: String) {
        viewModelScope.launch {
            _exerciseState.value = ExerciseState.Loading
            try {
                val token = UserSession.token ?: throw Exception("Token is null")
                api.deleteExercise("Bearer $token", id)
                fetchExercises()
            } catch (e: Exception) {
                _exerciseState.value = ExerciseState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun selectExerciseType(type: ExerciseType?) {
        _selectedExerciseType.value = type
    }

    fun updateSearchText(text: String) {
        _searchText.value = text
    }

//    fun getFilteredExerciseList(): List<Exercise> {
//        val type = _selectedExerciseType.value
//        val search = _searchText.value
//        return _exerciseList.value
//            .filter { type == null || it.exerciseType == type }
//            .filter {
//                search.isBlank() || it.name.contains(search, ignoreCase = true)
//                        || it.description.contains(search, ignoreCase = true)
//            }
//    }

    fun getExerciseById(id: String): Exercise? {
        return _exerciseList.value.find { it.id == id }
    }
}

sealed class ExerciseState {
    object Idle : ExerciseState()
    object Loading : ExerciseState()
    object Success : ExerciseState()
    data class Error(val message: String) : ExerciseState()
}
