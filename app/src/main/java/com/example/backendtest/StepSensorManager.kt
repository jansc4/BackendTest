package com.example.backendtest

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StepSensorManager(
    private val context: Context
) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

    private val _steps = MutableStateFlow(0)
    val steps: StateFlow<Int> = _steps.asStateFlow()

    private var manualSteps = 0

    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_STEP_DETECTOR) {
                manualSteps++
                _steps.value = manualSteps
                Log.d("StepSensor", "Step detected, total = $manualSteps")
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    fun startTracking() {
        if (stepDetectorSensor != null) {
            sensorManager.registerListener(
                sensorEventListener,
                stepDetectorSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        } else {
            Log.e("StepSensor", "TYPE_STEP_DETECTOR not available")
        }
    }

    fun stopTracking() {
        sensorManager.unregisterListener(sensorEventListener)
    }

    fun getManualSteps(): Int {
        return manualSteps
    }
}
