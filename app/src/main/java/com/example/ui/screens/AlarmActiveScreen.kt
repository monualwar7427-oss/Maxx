package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.TouchGuardViewModel
import com.example.ui.components.NumericPinKeypad
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AlertStrobeBlue
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AlarmActiveScreen(
    viewModel: TouchGuardViewModel,
    modifier: Modifier = Modifier
) {
    val triggerReason by viewModel.triggerReason.collectAsState()
    val enteredPin by viewModel.enteredPin.collectAsState()
    val isPinError by viewModel.pinError.collectAsState()
    val failedAttempts by viewModel.failedAttempts.collectAsState()
    val prefs = viewModel.prefs

    // High intensity flashing red/blue strobe animation
    val infiniteTransition = rememberInfiniteTransition(label = "strobe")
    val strobePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(250, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "phase"
    )

    val strobeBg = if (strobePhase > 0.5f) {
        AlertRed.copy(alpha = 0.85f)
    } else {
        AlertStrobeBlue.copy(alpha = 0.85f)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(strobeBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(CyberBackground.copy(alpha = 0.75f))
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Flashing Siren Banner
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = AlertRed,
                    modifier = Modifier.size(76.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = "Siren Alert",
                            tint = Color.White,
                            modifier = Modifier.size(46.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "🚨 ALARM TRIGGERED! 🚨",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "AAPKA PHONE KISI NE CHHUA HAI!",
                    color = CyberCyan,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = triggerReason.ifEmpty { "Unauthorized motion detected!" },
                    color = TextPrimary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Enter PIN to Disarm
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("alarm_pin_container"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface.copy(alpha = 0.95f)),
                border = androidx.compose.foundation.BorderStroke(2.dp, AlertRed)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ENTER PIN TO STOP SIREN",
                        color = AlertRed,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "Owner PIN is required to disarm (Default: 1234)",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
                    )

                    NumericPinKeypad(
                        currentPin = enteredPin,
                        onPinDigit = { viewModel.enterPinDigit(it) },
                        onBackspace = { viewModel.backspacePin() },
                        onClear = { viewModel.clearPin() },
                        isError = isPinError
                    )

                    if (failedAttempts > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Failed attempts: $failedAttempts",
                            color = AlertRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
