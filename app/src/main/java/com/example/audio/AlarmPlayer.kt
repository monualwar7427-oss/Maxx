package com.example.audio

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class AlarmPlayer(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager

    private var audioTrack: AudioTrack? = null
    private var isAlarmPlaying = false

    private var audioJob: Job? = null
    private var strobeJob: Job? = null
    private var vibrationJob: Job? = null

    private var originalVolume = 0
    private var rearCameraId: String? = null

    init {
        try {
            cameraManager?.cameraIdList?.forEach { id ->
                val characteristics = cameraManager.getCameraCharacteristics(id)
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                val hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) ?: false
                if (facing == CameraCharacteristics.LENS_FACING_BACK && hasFlash) {
                    rearCameraId = id
                }
            }
        } catch (_: Exception) {}
    }

    fun startAlarm(
        tone: String = "POLICE_SIREN",
        enableStrobe: Boolean = true,
        enableVibration: Boolean = true,
        scope: CoroutineScope
    ) {
        if (isAlarmPlaying) return
        isAlarmPlaying = true

        // 1. Maximize Volume
        audioManager?.let { am ->
            originalVolume = am.getStreamVolume(AudioManager.STREAM_ALARM)
            val maxAlarmVol = am.getStreamMaxVolume(AudioManager.STREAM_ALARM)
            val maxMusicVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            am.setStreamVolume(AudioManager.STREAM_ALARM, maxAlarmVol, 0)
            am.setStreamVolume(AudioManager.STREAM_MUSIC, maxMusicVol, 0)
        }

        // 2. Play Audio Tone Synthesizer in background
        audioJob = scope.launch(Dispatchers.Default) {
            playSirenLoop(tone)
        }

        // 3. Strobe Flashlight
        if (enableStrobe && rearCameraId != null) {
            strobeJob = scope.launch(Dispatchers.Default) {
                var torchOn = false
                while (isActive && isAlarmPlaying) {
                    try {
                        torchOn = !torchOn
                        rearCameraId?.let { cameraManager?.setTorchMode(it, torchOn) }
                    } catch (_: CameraAccessException) {
                        break
                    } catch (_: Exception) {
                        break
                    }
                    delay(120) // Flash strobe frequency ~8Hz
                }
                // Turn off torch when done
                try {
                    rearCameraId?.let { cameraManager?.setTorchMode(it, false) }
                } catch (_: Exception) {}
            }
        }

        // 4. Intense Vibration Pattern
        if (enableVibration) {
            vibrationJob = scope.launch(Dispatchers.Default) {
                val pattern = longArrayOf(0, 350, 120, 350, 120, 600)
                val amplitudes = intArrayOf(0, 255, 0, 255, 0, 255)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                    val effect = VibrationEffect.createWaveform(pattern, amplitudes, 0)
                    vm?.vibrate(CombinedVibration.createParallel(effect))
                } else {
                    @Suppress("DEPRECATION")
                    val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v?.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, 0))
                    } else {
                        @Suppress("DEPRECATION")
                        v?.vibrate(pattern, 0)
                    }
                }
            }
        }
    }

    private suspend fun playSirenLoop(tone: String) {
        val sampleRate = 44100
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ).coerceAtLeast(4096)

        try {
            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.play()

            val samples = ShortArray(bufferSize / 2)
            var phase = 0.0

            var cycle = 0
            while (isAlarmPlaying) {
                cycle++
                val freq = when (tone) {
                    "TACTICAL_WARBLE" -> {
                        // 800Hz to 1900Hz fast triangle wave sweep
                        val mod = (cycle % 40) / 40.0
                        800.0 + (if (mod < 0.5) mod * 2 else (1.0 - mod) * 2) * 1100.0
                    }
                    "PULSE_ALARM" -> {
                        // Stutter 1800Hz
                        if ((cycle % 10) < 6) 1800.0 else 0.0
                    }
                    "AIR_RAID" -> {
                        // Slow deep howling wail
                        val mod = (cycle % 120) / 120.0
                        500.0 + (if (mod < 0.5) mod * 2 else (1.0 - mod) * 2) * 900.0
                    }
                    else -> {
                        // European Police 2-tone hi-lo wail (960Hz / 770Hz)
                        if ((cycle / 20) % 2 == 0) 960.0 else 770.0
                    }
                }

                if (freq == 0.0) {
                    samples.fill(0)
                } else {
                    val phaseIncrement = 2 * PI * freq / sampleRate
                    for (i in samples.indices) {
                        samples[i] = (sin(phase) * 32767).toInt().toShort()
                        phase += phaseIncrement
                        if (phase > 2 * PI) phase -= 2 * PI
                    }
                }

                audioTrack?.write(samples, 0, samples.size)
                delay(15)
            }
        } catch (_: Exception) {
        } finally {
            try {
                audioTrack?.stop()
                audioTrack?.release()
            } catch (_: Exception) {}
            audioTrack = null
        }
    }

    fun stopAlarm() {
        isAlarmPlaying = false

        audioJob?.cancel()
        audioJob = null

        strobeJob?.cancel()
        strobeJob = null

        vibrationJob?.cancel()
        vibrationJob = null

        try {
            rearCameraId?.let { cameraManager?.setTorchMode(it, false) }
        } catch (_: Exception) {}

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.cancel()
            } else {
                @Suppress("DEPRECATION")
                val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                v?.cancel()
            }
        } catch (_: Exception) {}

        try {
            audioTrack?.pause()
            audioTrack?.flush()
            audioTrack?.stop()
            audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null
    }

    val isPlaying: Boolean
        get() = isAlarmPlaying
}
