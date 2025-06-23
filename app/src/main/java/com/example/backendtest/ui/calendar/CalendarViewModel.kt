package com.example.backendtest.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.backendtest.data.model.*
import com.example.backendtest.data.network.ApiService
import com.example.backendtest.data.network.ExercisePerformanceRequest
import com.example.backendtest.data.network.RetrofitInstance
import com.example.backendtest.data.network.UserSession
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class CalendarViewModel(
    private val api: ApiService = RetrofitInstance.api
) : ViewModel() {

    private val _exerciseList = MutableStateFlow<List<Exercise>>(emptyList())
    val exerciseList: StateFlow<List<Exercise>> = _exerciseList.asStateFlow()

    private val _calendarEntries = MutableStateFlow<List<ExercisePerformance>>(emptyList())
    val calendarEntries: StateFlow<List<ExercisePerformance>> = _calendarEntries.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _selectedExercisePerformance = MutableStateFlow<ExercisePerformance?>(null)
    val selectedExercisePerformance: StateFlow<ExercisePerformance?> = _selectedExercisePerformance.asStateFlow()

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    private val _newPerformanceExerciseId = MutableStateFlow<String?>(null)
    val newPerformanceExerciseId: StateFlow<String?> = _newPerformanceExerciseId.asStateFlow()

    // Wyświetlanie ćwiczeń z nazwą
    val performancesWithNames: StateFlow<List<ExercisePerformanceDisplay>> = combine(
        _calendarEntries,
        _exerciseList
    ) { performances, exercises ->
        performances.map { perf ->
            val exerciseName = exercises.find { it.id == perf.exercise_id }?.name ?: "Nieznane ćwiczenie"
            ExercisePerformanceDisplay(perf, exerciseName)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        fetchExercises()
        fetchCalendarEntriesForDate(_selectedDate.value)
    }

    fun fetchExercises() {
        viewModelScope.launch {
            runCatching {
                val token = UserSession.token ?: throw Exception("Brak tokena")
                api.getExercisesList("Bearer $token")
            }.onSuccess {
                _exerciseList.value = it
            }.onFailure {
                println("Błąd pobierania ćwiczeń: ${it.message}")
            }
        }
    }

    fun fetchCalendarEntriesForDate(date: LocalDate) {
        viewModelScope.launch {
            println("Fetching calendar entries for date: $date")
            runCatching {
                val token = UserSession.token ?: throw Exception("Brak tokena")
                val response = api.getCalendarEntryByDate("Bearer $token", date)
                println("Response received: $response")
                response
            }.onSuccess { response ->
                _calendarEntries.value = response.flatMap { day ->
                    day.exercises.map { ep ->
                        ExercisePerformance(
                            id = ep.id ?: "",
                            exercise_id = ep.exercise_id,
                            duration_min = ep.duration_min,
                            numberOfSets = ep.numberOfSets,
                            numberOfRepetitions = ep.numberOfRepetitions,
                            weight = ep.weight,
                            intervalBetween_days = ep.intervalBetween_days,
                            notes = ep.notes,
                            done = ep.done
                        )
                    }
                }
            }.onFailure {
                println("Błąd pobierania wpisów kalendarza: ${it.message}")
            }
        }
    }

    // Dodaj ćwiczenie (dla UI bez daty)
    fun addExerciseToCalendar(data: ExercisePerformanceData) {
        addExerciseToCalendar(data, _selectedDate.value)
    }

    fun addExerciseToCalendar(data: ExercisePerformanceData, date: LocalDate) {
        viewModelScope.launch {
            runCatching {
                val token = UserSession.token ?: throw Exception("Brak tokena")
                val request = ExercisePerformanceRequest(date, listOf(data))
                api.addCalendarDay("Bearer $token", request)
            }.onSuccess {
                fetchCalendarEntriesForDate(date)
                closeAddDialog()
            }.onFailure {
                println("Błąd dodawania ćwiczenia: ${it.message}")
            }
        }
    }

    fun deleteExerciseFromCalendar(performance: ExercisePerformance) {
        viewModelScope.launch {
            runCatching {
                val token = UserSession.token ?: throw Exception("Brak tokena")
                api.deleteCalendarEntryById("Bearer $token", performance.id)
            }.onSuccess {
                fetchCalendarEntriesForDate(_selectedDate.value)
            }.onFailure {
                println("Błąd usuwania ćwiczenia: ${it.message}")
            }
        }
    }

    fun toggleDoneStatus(performance: ExercisePerformanceDisplay, isDone: Boolean) {
        viewModelScope.launch {
            runCatching {
                val token = UserSession.token ?: throw Exception("Brak tokena")
                val date = _selectedDate.value

                // Pobierz aktualne dane kalendarza na dany dzień
                val dayResponses = api.getCalendarEntryByDate("Bearer $token", date)

                val currentDay = dayResponses.firstOrNull()
                    ?: throw Exception("Brak danych kalendarza dla daty $date")
                //Zmien status wybranego wykonania cwiczenia
                val updatedExercises = currentDay.exercises.map { ep ->
                    if (ep.id == performance.id) {
                        ep.copy(done = isDone)
                    } else {
                        ep
                    }
                }

                val request = ExercisePerformanceRequest(
                    date = date,
                    exercises = updatedExercises
                )

                // Aktualizacja całego dnia
                api.updateCalendarById("Bearer $token", request, currentDay.id)
            }.onSuccess {
                fetchCalendarEntriesForDate(_selectedDate.value)
            }.onFailure {
                println("Błąd zmiany statusu ćwiczenia: ${it.message}")
            }
        }
    }

    fun selectPreviousDay() {
        _selectedDate.value = _selectedDate.value.minusDays(1)
        fetchCalendarEntriesForDate(_selectedDate.value)
    }

    fun selectNextDay() {
        _selectedDate.value = _selectedDate.value.plusDays(1)
        fetchCalendarEntriesForDate(_selectedDate.value)
    }

    fun selectExercise(performance: ExercisePerformanceDisplay) {
        _selectedExercisePerformance.value = performance.performance
    }

    fun clearSelectedExercise() {
        _selectedExercisePerformance.value = null
    }

    fun openAddDialog() {
        _newPerformanceExerciseId.value = null
        _showAddDialog.value = true
    }

    fun closeAddDialog() {
        _showAddDialog.value = false
    }

    fun selectExerciseForNewPerformance(exerciseId: String) {
        _newPerformanceExerciseId.value = exerciseId
    }
}
