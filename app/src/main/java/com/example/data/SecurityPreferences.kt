package com.example.data

import android.content.Context
import android.content.SharedPreferences

class SecurityPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("touch_guard_prefs", Context.MODE_PRIVATE)

    var ownerName: String
        get() = prefs.getString(KEY_OWNER_NAME, "Sumit") ?: "Sumit"
        set(value) = prefs.edit().putString(KEY_OWNER_NAME, value).apply()

    var pinCode: String
        get() = prefs.getString(KEY_PIN_CODE, "1234") ?: "1234"
        set(value) = prefs.edit().putString(KEY_PIN_CODE, value).apply()

    var sensitivityLevel: Int
        get() = prefs.getInt(KEY_SENSITIVITY_LEVEL, 2) // 1 = High, 2 = Medium, 3 = Low
        set(value) = prefs.edit().putInt(KEY_SENSITIVITY_LEVEL, value).apply()

    val motionThreshold: Float
        get() = when (sensitivityLevel) {
            1 -> 0.7f // Very sensitive - detects soft touch
            3 -> 2.5f // Less sensitive - requires deliberate movement
            else -> 1.4f // Normal desk detection
        }

    var armingDelaySeconds: Int
        get() = prefs.getInt(KEY_ARMING_DELAY, 5)
        set(value) = prefs.edit().putInt(KEY_ARMING_DELAY, value).apply()

    var sirenTone: String
        get() = prefs.getString(KEY_SIREN_TONE, "POLICE_SIREN") ?: "POLICE_SIREN"
        set(value) = prefs.edit().putString(KEY_SIREN_TONE, value).apply()

    var voiceAlertEnabled: Boolean
        get() = prefs.getBoolean(KEY_VOICE_ALERT, true)
        set(value) = prefs.edit().putBoolean(KEY_VOICE_ALERT, value).apply()

    var flashlightStrobeEnabled: Boolean
        get() = prefs.getBoolean(KEY_FLASHLIGHT_STROBE, true)
        set(value) = prefs.edit().putBoolean(KEY_FLASHLIGHT_STROBE, value).apply()

    var vibrationEnabled: Boolean
        get() = prefs.getBoolean(KEY_VIBRATION, true)
        set(value) = prefs.edit().putBoolean(KEY_VIBRATION, value).apply()

    var stealthModeEnabled: Boolean
        get() = prefs.getBoolean(KEY_STEALTH_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_STEALTH_MODE, value).apply()

    var pocketGuardEnabled: Boolean
        get() = prefs.getBoolean(KEY_POCKET_GUARD, false)
        set(value) = prefs.edit().putBoolean(KEY_POCKET_GUARD, value).apply()

    var chargerGuardEnabled: Boolean
        get() = prefs.getBoolean(KEY_CHARGER_GUARD, false)
        set(value) = prefs.edit().putBoolean(KEY_CHARGER_GUARD, value).apply()

    var intruderCaptureEnabled: Boolean
        get() = prefs.getBoolean(KEY_INTRUDER_CAPTURE, true)
        set(value) = prefs.edit().putBoolean(KEY_INTRUDER_CAPTURE, value).apply()

    companion object {
        private const val KEY_OWNER_NAME = "owner_name"
        private const val KEY_PIN_CODE = "pin_code"
        private const val KEY_SENSITIVITY_LEVEL = "sensitivity_level"
        private const val KEY_ARMING_DELAY = "arming_delay"
        private const val KEY_SIREN_TONE = "siren_tone"
        private const val KEY_VOICE_ALERT = "voice_alert"
        private const val KEY_FLASHLIGHT_STROBE = "flashlight_strobe"
        private const val KEY_VIBRATION = "vibration"
        private const val KEY_STEALTH_MODE = "stealth_mode"
        private const val KEY_POCKET_GUARD = "pocket_guard"
        private const val KEY_CHARGER_GUARD = "charger_guard"
        private const val KEY_INTRUDER_CAPTURE = "intruder_capture"
    }
}
