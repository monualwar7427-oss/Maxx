package com.example

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AlarmPlayer
import com.example.data.IntruderIncident
import com.example.data.IntruderLogRepository
import com.example.data.SecurityPreferences
import com.example.sensor.TouchGuardDetector
import com.example.service.TouchGuardService
import com.example.voice.MaxAssistant
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class GuardStatus {
    IDLE,
    COUNTDOWN,
    ARMED,
    ALARM_ACTIVE
}

class TouchGuardViewModel(application: Application) : AndroidViewModel(application) {

    val prefs = SecurityPreferences(application)
    val logRepo = IntruderLogRepository(application)
    val maxAssistant = MaxAssistant(application)
    private val alarmPlayer = AlarmPlayer(application)

    private val _status = MutableStateFlow(GuardStatus.IDLE)
    val status: StateFlow<GuardStatus> = _status.asStateFlow()

    private val _countdownRemaining = MutableStateFlow(5)
    val countdownRemaining: StateFlow<Int> = _countdownRemaining.asStateFlow()

    private val _triggerReason = MutableStateFlow("")
    val triggerReason: StateFlow<String> = _triggerReason.asStateFlow()

    private val _enteredPin = MutableStateFlow("")
    val enteredPin: StateFlow<String> = _enteredPin.asStateFlow()

    private val _pinError = MutableStateFlow(false)
    val pinError: StateFlow<Boolean> = _pinError.asStateFlow()

    private val _failedAttempts = MutableStateFlow(0)
    val failedAttempts: StateFlow<Int> = _failedAttempts.asStateFlow()

    private val _selectedTab = MutableStateFlow(0) // 0: Home, 1: Max AI, 2: Logs, 3: Settings
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _incidents = MutableStateFlow<List<IntruderIncident>>(emptyList())
    val incidents: StateFlow<List<IntruderIncident>> = _incidents.asStateFlow()

    private var countdownJob: Job? = null
    private var alarmVoiceRepeatJob: Job? = null

    val detector = TouchGuardDetector(application) { reason ->
        onMotionOrSensorTriggered(reason)
    }

    val liveMotionLevel: StateFlow<Float> = detector.liveMotionLevel

    init {
        refreshIncidents()
    }

    fun setSelectedTab(tab: Int) {
        _selectedTab.value = tab
    }

    fun refreshIncidents() {
        _incidents.value = logRepo.getIncidents()
    }

    fun clearIncidents() {
        logRepo.clearAll()
        _incidents.value = emptyList()
    }

    /**
     * Arms Touch Guard with countdown delay so owner has time to place phone down.
     */
    fun startArming() {
        if (_status.value == GuardStatus.ARMED || _status.value == GuardStatus.COUNTDOWN) return

        val delaySec = prefs.armingDelaySeconds
        _countdownRemaining.value = delaySec
        _status.value = GuardStatus.COUNTDOWN

        // Max announces countdown
        maxAssistant.announceArmingCountdown(delaySec)

        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            for (i in delaySec downTo 1) {
                _countdownRemaining.value = i
                delay(1000)
            }
            // Arm now
            completeArming()
        }
    }

    fun cancelArming() {
        countdownJob?.cancel()
        countdownJob = null
        _status.value = GuardStatus.IDLE
        maxAssistant.speak("Touch Guard arming cancel ho gaya hai.")
    }

    private fun completeArming() {
        _status.value = GuardStatus.ARMED
        TouchGuardService.start(getApplication())

        // Start sensor monitoring
        detector.startMonitoring(
            threshold = prefs.motionThreshold,
            enablePocketMode = prefs.pocketGuardEnabled
        )

        // Voice announcement like in the video: "Kar diya Sumit, Touch Guard on ho gaya hai."
        maxAssistant.announceArming(prefs.ownerName)
    }

    private fun onMotionOrSensorTriggered(reason: String) {
        if (_status.value != GuardStatus.ARMED) return

        _triggerReason.value = reason
        _status.value = GuardStatus.ALARM_ACTIVE
        _enteredPin.value = ""
        _pinError.value = false

        // Start Siren Audio, Flash Strobe, Vibration
        alarmPlayer.startAlarm(
            tone = prefs.sirenTone,
            enableStrobe = prefs.flashlightStrobeEnabled,
            enableVibration = prefs.vibrationEnabled,
            scope = viewModelScope
        )

        // Repeated voice alert: "Alert! Aapka phone kisi ne chhua hai!"
        if (prefs.voiceAlertEnabled) {
            alarmVoiceRepeatJob?.cancel()
            alarmVoiceRepeatJob = viewModelScope.launch {
                while (_status.value == GuardStatus.ALARM_ACTIVE) {
                    maxAssistant.announceIntruderAlert(prefs.ownerName)
                    delay(3500)
                }
            }
        }

        // Record incident in log
        val incident = IntruderIncident(
            triggerReason = reason,
            failedPinAttempts = 0
        )
        logRepo.addIncident(incident)
        refreshIncidents()
    }

    fun triggerManualAlarm(reason: String = "Test Alarm Triggered") {
        onMotionOrSensorTriggered(reason)
    }

    fun enterPinDigit(digit: String) {
        if (_enteredPin.value.length < 4) {
            val updated = _enteredPin.value + digit
            _enteredPin.value = updated
            if (updated.length == 4) {
                verifyPin(updated)
            }
        }
    }

    fun backspacePin() {
        if (_enteredPin.value.isNotEmpty()) {
            _enteredPin.value = _enteredPin.value.dropLast(1)
            _pinError.value = false
        }
    }

    fun clearPin() {
        _enteredPin.value = ""
        _pinError.value = false
    }

    private fun verifyPin(pin: String) {
        val correctPin = prefs.pinCode
        if (pin == correctPin) {
            // Correct PIN! Disarm everything
            disarm()
        } else {
            // Wrong PIN!
            _pinError.value = true
            _failedAttempts.value += 1
            maxAssistant.announceWrongPin()
            viewModelScope.launch {
                delay(600)
                _enteredPin.value = ""
                _pinError.value = false
            }
        }
    }

    fun disarm() {
        countdownJob?.cancel()
        alarmVoiceRepeatJob?.cancel()
        detector.stopMonitoring()
        alarmPlayer.stopAlarm()
        TouchGuardService.stop(getApplication())

        _status.value = GuardStatus.IDLE
        _enteredPin.value = ""
        _pinError.value = false
        _failedAttempts.value = 0

        maxAssistant.announceDisarmed(prefs.ownerName)
    }

    fun processVoiceCommand(command: String) {
        maxAssistant.processCommand(
            command = command,
            ownerName = prefs.ownerName,
            onArmRequested = { startArming() },
            onDisarmRequested = { disarm() }
        )
    }

    override fun onCleared() {
        detector.stopMonitoring()
        alarmPlayer.stopAlarm()
        maxAssistant.destroy()
        TouchGuardService.stop(getApplication())
        super.onCleared()
    }
}
