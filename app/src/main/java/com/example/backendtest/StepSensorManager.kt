package com.example.backendtest

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// StepSensorManager.kt
class StepSensorManager(private val context: Context) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private val _steps = MutableStateFlow(0)
    val steps: StateFlow<Int> = _steps.asStateFlow()

    private var initialSteps: Int? = null

    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val totalSteps = event.values[0].toInt()

            if (initialSteps == null) {
                initialSteps = totalSteps
            }

            // Oblicz kroki wykonane dzisiaj
            val currentSteps = totalSteps - (initialSteps ?: totalSteps)
            _steps.value = currentSteps
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // Ignorujemy zmiany dokładności
        }
    }

    fun startTracking() {
        sensorManager.registerListener(
            sensorEventListener,
            stepSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    fun stopTracking() {
        sensorManager.unregisterListener(sensorEventListener)
    }
}