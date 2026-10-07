package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.TouchGuardViewModel
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberCyanGlow
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.ShieldGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MaxVoiceScreen(
    viewModel: TouchGuardViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val maxAssistant = viewModel.maxAssistant
    val isListening by maxAssistant.isListening.collectAsState()
    val lastResponse by maxAssistant.lastVoiceResponse.collectAsState()
    val recognizedText by maxAssistant.recognizedText.collectAsState()
    val prefs = viewModel.prefs
    val ownerName = prefs.ownerName

    var speechError by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            maxAssistant.startListening(
                onResult = { text ->
                    speechError = null
                    viewModel.processVoiceCommand(text)
                },
                onError = { err ->
                    speechError = err
                }
            )
        } else {
            speechError = "Microphone permission is required for voice commands"
        }
    }

    // Orb pulsating animation
    val infiniteTransition = rememberInfiniteTransition(label = "orb_pulse")
    val orbScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SmartToy,
                contentDescription = null,
                tint = CyberCyan,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "MAX AI VOICE GUARDIAN",
                color = CyberCyan,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp
            )
        }

        // Animated Max AI Voice Orb
        Box(
            modifier = Modifier
                .size(190.dp)
                .testTag("max_voice_orb"),
            contentAlignment = Alignment.Center
        ) {
            // Glow layer
            Box(
                modifier = Modifier
                    .size((170 * (if (isListening) orbScale else 1f)).dp)
                    .clip(CircleShape)
                    .background(if (isListening) AlertRed.copy(alpha = 0.25f) else CyberCyanGlow)
            )

            // Inner Orb
            Surface(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .clickable {
                        if (isListening) {
                            maxAssistant.stopListening()
                        } else {
                            val hasPermission = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            ) == PackageManager.PERMISSION_GRANTED

                            if (hasPermission) {
                                maxAssistant.startListening(
                                    onResult = { text ->
                                        speechError = null
                                        viewModel.processVoiceCommand(text)
                                    },
                                    onError = { err ->
                                        speechError = err
                                    }
                                )
                            } else {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                    }
                    .testTag("max_mic_orb_button"),
                shape = CircleShape,
                color = if (isListening) AlertRed else CyberSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(
                    2.dp,
                    if (isListening) AlertRed else CyberCyan
                )
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.GraphicEq else Icons.Default.Mic,
                            contentDescription = "Speak to Max",
                            tint = if (isListening) Color.White else CyberCyan,
                            modifier = Modifier.size(46.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isListening) "SUN RAHA HOON..." else "TAP TO SPEAK",
                            color = if (isListening) Color.White else TextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = if (isListening) "Max is listening to your command..." else "Tap orb or say: \"Max, main nahane ja raha hoon...\"",
            color = if (isListening) AlertRed else TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )

        speechError?.let { err ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = err,
                color = AlertRed,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Dialogue bubble
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "MAX'S RESPONSE",
                        color = CyberCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = lastResponse ?: "Ji $ownerName, main active hoon. Aap boliye kya madad chahiye?",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp
                )

                if (recognizedText != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You said: \"$recognizedText\"",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Command Pills
        Text(
            text = "QUICK VOICE COMMANDS (TAP TO SIMULATE)",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        )

        val quickCommands = listOf(
            "Max, main nahane ja raha hoon",
            "Touch Guard mode on karo",
            "Status batao Max",
            "Disarm karo guard",
            "Namaste Max"
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickCommands.forEach { cmd ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = CyberSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                    modifier = Modifier
                        .clickable {
                            viewModel.processVoiceCommand(cmd)
                        }
                        .testTag("quick_cmd_${cmd.take(10)}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = cmd,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
