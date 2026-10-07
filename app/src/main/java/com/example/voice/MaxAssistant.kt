package com.example.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class MaxAssistant(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _lastVoiceResponse = MutableStateFlow<String?>(null)
    val lastVoiceResponse: StateFlow<String?> = _lastVoiceResponse.asStateFlow()

    private val _recognizedText = MutableStateFlow<String?>(null)
    val recognizedText: StateFlow<String?> = _recognizedText.asStateFlow()

    private var speechRecognizer: SpeechRecognizer? = null

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val hindi = Locale("hi", "IN")
            val result = tts?.setLanguage(hindi)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to Indian English or default
                tts?.setLanguage(Locale("en", "IN"))
            }
            tts?.setPitch(1.05f)
            tts?.setSpeechRate(1.0f)
            isTtsReady = true
        }
    }

    fun speak(text: String, queueMode: Int = TextToSpeech.QUEUE_FLUSH) {
        _lastVoiceResponse.value = text
        if (isTtsReady && tts != null) {
            tts?.speak(text, queueMode, null, "MAX_${System.currentTimeMillis()}")
        }
    }

    fun stopSpeaking() {
        tts?.stop()
    }

    fun announceArming(ownerName: String) {
        speak("Kar diya $ownerName, Touch Guard on ho gaya hai. Phone ko table par chhod dijiye.")
    }

    fun announceArmingCountdown(seconds: Int) {
        speak("Touch Guard $seconds second mein active hoga.")
    }

    fun announceIntruderAlert(ownerName: String) {
        speak("Alert! Aapka phone kisi ne chhua hai! Chhod mera phone!", TextToSpeech.QUEUE_ADD)
    }

    fun announceDisarmed(ownerName: String) {
        speak("Touch Guard disarm ho gaya hai. Welcome back $ownerName!")
    }

    fun announceWrongPin() {
        speak("Galat PIN code! Access denied!")
    }

    fun greet(ownerName: String) {
        speak("Ji $ownerName, bolie. Max aapki madad ke liye taiyar hai.")
    }

    /**
     * Interprets natural language voice/text commands.
     * Returns true if command triggered arming.
     */
    fun processCommand(
        command: String,
        ownerName: String,
        onArmRequested: () -> Unit,
        onDisarmRequested: () -> Unit
    ) {
        val lower = command.lowercase(Locale.ROOT).trim()
        _recognizedText.value = command

        when {
            lower.contains("nahan") || lower.contains("bath") || lower.contains("wash") -> {
                speak("Ji $ownerName, aap aaram se jaiye. Main aapke phone par pehra dunga. Touch Guard on ho raha hai.")
                onArmRequested()
            }
            lower.contains("touch guard on") || lower.contains("guard on") || lower.contains("on karo") ||
                    lower.contains("activate") || lower.contains("chalu karo") || lower.contains("suraksha") -> {
                announceArming(ownerName)
                onArmRequested()
            }
            lower.contains("touch guard off") || lower.contains("guard off") || lower.contains("band karo") ||
                    lower.contains("disarm") || lower.contains("hatao") -> {
                onDisarmRequested()
            }
            lower.contains("status") || lower.contains("haal") || lower.contains("check") -> {
                speak("Main online hoon. Sab surakshit hai $ownerName.")
            }
            lower.contains("kya kar sakte") || lower.contains("help") || lower.contains("madad") -> {
                speak("Main aapka phone chori aur unauthorised touch se bachata hoon. Aap bol sakte hain: Max, Touch Guard on karo.")
            }
            lower.contains("namaste") || lower.contains("hello") || lower.contains("hi") || lower.contains("max") -> {
                greet(ownerName)
            }
            else -> {
                speak("Samajh gaya $ownerName. Touch Guard activate kar raha hoon.")
                onArmRequested()
            }
        }
    }

    fun startListening(onResult: (String) -> Unit, onError: (String) -> Unit) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onError("Speech recognition not available on this device")
            return
        }

        try {
            stopListening()
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _isListening.value = true
                    }
                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {
                        _isListening.value = false
                    }
                    override fun onError(error: Int) {
                        _isListening.value = false
                        onError("Mic error code: $error")
                    }
                    override fun onResults(results: Bundle?) {
                        _isListening.value = false
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull() ?: ""
                        if (text.isNotEmpty()) {
                            onResult(text)
                        }
                    }
                    override fun onPartialResults(partialResults: Bundle?) {}
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Max sun raha hai... Bolie!")
            }
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            _isListening.value = false
            onError(e.message ?: "Speech recognition error")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.destroy()
        } catch (_: Exception) {}
        speechRecognizer = null
        _isListening.value = false
    }

    fun destroy() {
        tts?.stop()
        tts?.shutdown()
        stopListening()
    }
}
