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
    private var sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var stepSensor: Sensor? = null
    private var initialSteps = -1
    private var _currentSteps = MutableStateFlow(0)
    val currentSteps: StateFlow<Int> = _currentSteps.asStateFlow()

    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (initialSteps == -1) {
                initialSteps = event.values[0].toInt()
            }
            _currentSteps.value = event.values[0].toInt() - initialSteps
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    fun startCounting() {
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepSensor?.let {
            sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stopCounting() {
        sensorManager.unregisterListener(sensorEventListener)
    }
}