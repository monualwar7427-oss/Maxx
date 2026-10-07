package com.example.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs
import kotlin.math.sqrt

class TouchGuardDetector(
    private val context: Context,
    private val onTriggered: (reason: String) -> Unit
) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager

    private val accelerometer: Sensor? =
        sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val proximitySensor: Sensor? =
        sensorManager?.getDefaultSensor(Sensor.TYPE_PROXIMITY)

    private var isArmed = false
    private var pocketModeEnabled = false

    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f
    private var isCalibrated = false
    private var calibrationReadings = 0

    // Live telemetry for UI
    private val _liveMotionLevel = MutableStateFlow(0f)
    val liveMotionLevel: StateFlow<Float> = _liveMotionLevel.asStateFlow()

    private var motionThreshold = 1.4f
    private var initialProximity = -1f

    fun startMonitoring(threshold: Float, enablePocketMode: Boolean = false) {
        this.motionThreshold = threshold
        this.pocketModeEnabled = enablePocketMode
        this.isArmed = true
        this.isCalibrated = false
        this.calibrationReadings = 0
        this.initialProximity = -1f

        accelerometer?.let {
            sensorManager?.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_GAME
            )
        }

        if (enablePocketMode) {
            proximitySensor?.let {
                sensorManager?.registerListener(
                    this,
                    it,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            }
        }
    }

    fun stopMonitoring() {
        isArmed = false
        _liveMotionLevel.value = 0f
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (!isArmed || event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                if (!isCalibrated) {
                    lastX = x
                    lastY = y
                    lastZ = z
                    calibrationReadings++
                    if (calibrationReadings > 5) {
                        isCalibrated = true
                    }
                    return
                }

                val dx = abs(x - lastX)
                val dy = abs(y - lastY)
                val dz = abs(z - lastZ)
                val delta = sqrt((dx * dx + dy * dy + dz * dz).toDouble()).toFloat()

                // Smooth telemetry
                _liveMotionLevel.value = delta

                if (delta > motionThreshold) {
                    onTriggered("Motion Detected (Moved / Picked up)")
                }

                // Low-pass filter baseline update so slow gentle drift doesn't false-alarm,
                // but any quick touch triggers immediately
                lastX = lastX * 0.85f + x * 0.15f
                lastY = lastY * 0.85f + y * 0.15f
                lastZ = lastZ * 0.85f + z * 0.15f
            }

            Sensor.TYPE_PROXIMITY -> {
                val distance = event.values[0]
                val maxRange = event.sensor.maximumRange

                if (initialProximity < 0) {
                    initialProximity = distance
                    return
                }

                // If initially covered/in pocket (< 2cm) and now suddenly exposed (> 3cm)
                if (pocketModeEnabled && initialProximity < 3f && distance >= 3f) {
                    onTriggered("Pocket Removal Detected")
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
