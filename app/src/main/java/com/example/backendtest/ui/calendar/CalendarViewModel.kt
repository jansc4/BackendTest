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

    private val _selectedExercisePerformance = MutableStateFlow<ExercisePerformanceDisplay?>(null)
    val selectedExercisePerformance: StateFlow<ExercisePerformanceDisplay?> = _selectedExercisePerformance.asStateFlow()

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
        println("✅ CalendarViewModel initialized!")
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
                _calendarEntries.value = response.exercises.map { ep ->
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
            }.onFailure {
                if (it.message.equals("HTTP 404 Not Found") ){
                    _calendarEntries.value = emptyList()
                }
                println("Błąd pobierania wpisów kalendarza: ${it.message}")
                it.printStackTrace()
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
        _selectedExercisePerformance.value = performance
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

    fun addExerciseToCalendar(exercisePerformance: ExercisePerformanceData, date: LocalDate) {
        viewModelScope.launch {
            runCatching {
                val token = UserSession.token ?: throw Exception("Brak tokena")
                    api.addExerciseToCalendarByDate("Bearer $token", date, exercisePerformance)
//                }
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
                val date = _selectedDate.value
                val calendarDay = api.getCalendarEntryByDate("Bearer $token", date)
                api.deleteExerciseFromCalendarEntryByCalendarId("Bearer $token", calendarDay.id, performance.id)
            }.onSuccess {
                fetchCalendarEntriesForDate(_selectedDate.value)
            }.onFailure {
                println("Błąd usuwania ćwiczenia: ${it.message}")
            }
        }
    }

    fun updateExercisePerformance(data: ExercisePerformanceData, date: LocalDate) {
        viewModelScope.launch {
            runCatching {
                val token = UserSession.token ?: throw Exception("Brak tokena")
                val calendarDay = api.getCalendarEntryByDate("Bearer $token", date)
                val calendarDayId = calendarDay.id
                data.id?.let { exerciseId ->
                    api.updateExerciseInCalendarEntryByCalendarId("Bearer $token", data, calendarDayId, exerciseId)
                } ?: throw Exception("Brak id ćwiczenia do aktualizacji")
            }.onSuccess {
                fetchCalendarEntriesForDate(date)
                closeAddDialog()
                clearSelectedExercise()
            }.onFailure {
                println("Błąd aktualizacji ćwiczenia: ${it.message}")
            }
        }
    }

    fun toggleDoneStatus(performance: ExercisePerformanceDisplay, isDone: Boolean) {
        viewModelScope.launch {
            println("toggleDone for perf.id=${performance.id}, new done=$isDone")
            runCatching {
                val token = UserSession.token ?: throw Exception("Brak tokena")
                val date = _selectedDate.value
                println("Current date: $date")

                val calendarDay = api.getCalendarEntryByDate("Bearer $token", date)
                val calendarDayId = calendarDay.id
                println("calendarDay.id=${calendarDay.id}, exercises before: ${calendarDay.exercises.map { it.done }}\")")

                val perf = performance.performance
                val updatedData = ExercisePerformanceData(
                    id = perf.id,
                    exercise_id = perf.exercise_id,
                    duration_min = perf.duration_min,
                    numberOfSets = perf.numberOfSets,
                    numberOfRepetitions = perf.numberOfRepetitions,
                    weight = perf.weight,
                    intervalBetween_days = perf.intervalBetween_days,
                    notes = perf.notes,
                    done = isDone
                )

                val updatedPerf = api.updateExerciseInCalendarEntryByCalendarId(
                    authHeader = "Bearer $token",
                    body = updatedData,
                    id = calendarDayId,
                    exerciseId = perf.id
                )
                println("Updated perf: id=${updatedPerf.id}, done=${updatedPerf.done}")
            }.onSuccess {
                println("Refreshing list for new date")
                fetchCalendarEntriesForDate(_selectedDate.value)
            }.onFailure {
                println("Błąd zmiany statusu ćwiczenia: ${it.message}")
                it.printStackTrace()
            }
        }
    }


    fun deleteExercisePerformance(performance: ExercisePerformance) {
        viewModelScope.launch {
            runCatching {
                val token = UserSession.token ?: throw Exception("Brak tokena")
                api.deleteCalendarEntryByCalendarId("Bearer $token", performance.id)
            }.onSuccess {
                fetchCalendarEntriesForDate(_selectedDate.value)
            }.onFailure {
                println("Błąd usuwania wpisu kalendarza: ${it.message}")
            }
        }
    }
}
