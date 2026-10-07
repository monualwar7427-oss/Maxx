package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.TouchGuardViewModel
import com.example.ui.components.MotionMeter
import com.example.ui.components.NumericPinKeypad
import com.example.ui.components.RadarPulse
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

@Composable
fun ArmedScreen(
    viewModel: TouchGuardViewModel,
    modifier: Modifier = Modifier
) {
    val liveMotion by viewModel.liveMotionLevel.collectAsState()
    val enteredPin by viewModel.enteredPin.collectAsState()
    val isPinError by viewModel.pinError.collectAsState()
    val prefs = viewModel.prefs
    val ownerName = prefs.ownerName

    var showPinDialog by remember { mutableStateOf(false) }
    var stealthModeActive by remember { mutableStateOf(prefs.stealthModeEnabled) }

    if (stealthModeActive) {
        // Pitch black screen with tiny stealth lock to save battery and trap intruders!
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable { stealthModeActive = false }
                .testTag("stealth_screen"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Stealth Armed",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Stealth Mode (Tap to wake screen)",
                    color = Color.DarkGray,
                    fontSize = 11.sp
                )
            }
        }
        return
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = ShieldGreen.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ShieldGreen)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(ShieldGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "MAX GUARD ACTIVE",
                            color = ShieldGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Button(
                    onClick = { stealthModeActive = true },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                    modifier = Modifier.testTag("stealth_mode_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Stealth Display",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Radar & Security status
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Box(
                    modifier = Modifier.size(240.dp),
                    contentAlignment = Alignment.Center
                ) {
                    RadarPulse(
                        isArmed = true,
                        isAlarm = false,
                        modifier = Modifier.fillMaxSize()
                    )

                    Surface(
                        modifier = Modifier.size(110.dp),
                        shape = CircleShape,
                        color = CyberSurface,
                        border = androidx.compose.foundation.BorderStroke(2.dp, CyberCyan)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = CyberCyan,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "ARMED",
                                    color = CyberCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "DO NOT TOUCH THIS PHONE",
                    color = AlertRed,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Suraksha active hai $ownerName. Any movement triggers full siren!",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            // Live sensor telemetry
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    MotionMeter(
                        currentMotion = liveMotion,
                        threshold = prefs.motionThreshold
                    )
                }
            }

            // Disarm action button
            Button(
                onClick = { showPinDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("disarm_pin_button"),
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = CyberBackground,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "DISARM GUARD (ENTER PIN)",
                    color = CyberBackground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        // Disarm PIN entry overlay dialog
        if (showPinDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CyberBackground.copy(alpha = 0.95f))
                    .clickable { showPinDialog = false },
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .clickable(enabled = false) {},
                    shape = RoundedCornerShape(24.dp),
                    color = CyberSurface,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, CyberCyan)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ENTER SECURITY PIN",
                                color = CyberCyan,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = TextSecondary,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { showPinDialog = false }
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Default PIN is 1234",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        NumericPinKeypad(
                            currentPin = enteredPin,
                            onPinDigit = { viewModel.enterPinDigit(it) },
                            onBackspace = { viewModel.backspacePin() },
                            onClear = { viewModel.clearPin() },
                            isError = isPinError
                        )
                    }
                }
            }
        }
    }
}
