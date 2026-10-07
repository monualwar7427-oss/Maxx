package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.GuardStatus
import com.example.R
import com.example.TouchGuardViewModel
import com.example.ui.components.MotionMeter
import com.example.ui.components.RadarPulse
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AlertRedGlow
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
fun DashboardScreen(
    viewModel: TouchGuardViewModel,
    onNavigateToMax: () -> Unit,
    modifier: Modifier = Modifier
) {
    val status by viewModel.status.collectAsState()
    val countdown by viewModel.countdownRemaining.collectAsState()
    val liveMotion by viewModel.liveMotionLevel.collectAsState()
    val prefs = viewModel.prefs
    val ownerName = prefs.ownerName

    var pocketGuard by remember { mutableStateOf(prefs.pocketGuardEnabled) }
    var chargerGuard by remember { mutableStateOf(prefs.chargerGuardEnabled) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "TOUCHGUARD AI",
                    color = CyberCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Namaste $ownerName!",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = when (status) {
                    GuardStatus.ARMED -> ShieldGreen.copy(alpha = 0.2f)
                    GuardStatus.COUNTDOWN -> AlertRed.copy(alpha = 0.2f)
                    GuardStatus.ALARM_ACTIVE -> AlertRed.copy(alpha = 0.3f)
                    else -> CyberSurfaceVariant
                },
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    when (status) {
                        GuardStatus.ARMED -> ShieldGreen
                        GuardStatus.COUNTDOWN -> AlertRed
                        GuardStatus.ALARM_ACTIVE -> AlertRed
                        else -> CyberBorder
                    }
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                when (status) {
                                    GuardStatus.ARMED -> ShieldGreen
                                    GuardStatus.COUNTDOWN -> AlertRed
                                    GuardStatus.ALARM_ACTIVE -> AlertRed
                                    else -> TextMuted
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when (status) {
                            GuardStatus.ARMED -> "ARMED"
                            GuardStatus.COUNTDOWN -> "ARMING ($countdown s)"
                            GuardStatus.ALARM_ACTIVE -> "SIREN BLARING"
                            else -> "STANDBY"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (status) {
                            GuardStatus.ARMED -> ShieldGreen
                            GuardStatus.COUNTDOWN -> AlertRed
                            GuardStatus.ALARM_ACTIVE -> AlertRed
                            else -> TextSecondary
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Max Hero Interactive Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable { onNavigateToMax() }
                .testTag("max_hero_card"),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.hero_max_guardian),
                    contentDescription = "Max AI Guardian",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    contentScale = ContentScale.Crop
                )
                // Dark gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, CyberSurface.copy(alpha = 0.95f))
                            )
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "MAX VOICE ASSISTANT",
                                color = CyberCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "\"Max, main nahane ja raha hoon...\"",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = CyberCyan,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice with Max",
                                tint = CyberBackground,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Center Big Radar & Activation Button
        Box(
            modifier = Modifier
                .size(230.dp)
                .testTag("main_radar_container"),
            contentAlignment = Alignment.Center
        ) {
            RadarPulse(
                isArmed = status == GuardStatus.ARMED,
                isAlarm = status == GuardStatus.ALARM_ACTIVE,
                modifier = Modifier.fillMaxSize()
            )

            // Inner Action Circle
            Surface(
                modifier = Modifier
                    .size(148.dp)
                    .clip(CircleShape)
                    .clickable {
                        when (status) {
                            GuardStatus.IDLE -> viewModel.startArming()
                            GuardStatus.COUNTDOWN -> viewModel.cancelArming()
                            GuardStatus.ARMED -> viewModel.disarm()
                            GuardStatus.ALARM_ACTIVE -> viewModel.disarm()
                        }
                    }
                    .testTag("activate_guard_button"),
                shape = CircleShape,
                color = when (status) {
                    GuardStatus.ARMED -> AlertRed
                    GuardStatus.COUNTDOWN -> CyberSurfaceVariant
                    GuardStatus.ALARM_ACTIVE -> AlertRed
                    else -> CyberCyan
                },
                shadowElevation = 12.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = when (status) {
                                GuardStatus.ARMED -> Icons.Default.Shield
                                GuardStatus.COUNTDOWN -> Icons.Default.Warning
                                GuardStatus.ALARM_ACTIVE -> Icons.Default.Warning
                                else -> Icons.Default.Security
                            },
                            contentDescription = "Shield Guard",
                            tint = if (status == GuardStatus.COUNTDOWN) CyberCyan else CyberBackground,
                            modifier = Modifier.size(42.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (status) {
                                GuardStatus.ARMED -> "DISARM"
                                GuardStatus.COUNTDOWN -> "CANCEL ($countdown)"
                                GuardStatus.ALARM_ACTIVE -> "STOP ALARM"
                                else -> "ACTIVATE"
                            },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = if (status == GuardStatus.COUNTDOWN) CyberCyan else CyberBackground
                        )
                        Text(
                            text = when (status) {
                                GuardStatus.ARMED -> "TAP TO UNLOCK"
                                GuardStatus.COUNTDOWN -> "PLACE PHONE DOWN"
                                else -> "TOUCH GUARD"
                            },
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (status == GuardStatus.COUNTDOWN) TextSecondary else CyberBackground.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Countdown Instruction Card
        AnimatedVisibility(visible = status == GuardStatus.COUNTDOWN) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Smartphone,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Arming in $countdown seconds...",
                            color = CyberCyan,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Abhi phone ko table ya stand par rakh dein. Hilayein nahi!",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Live Motion Telemetry Gauge
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

        Spacer(modifier = Modifier.height(16.dp))

        // Guard Modes Switch Cards
        Text(
            text = "SECURITY PROTECTION MODES",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Mode 1: Desk / Table Motion Guard (Always Active)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberCyanGlow),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Smartphone,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Desk & Motion Guard",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Triggers if someone picks up or moves phone",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
                Text(
                    text = "ACTIVE",
                    color = CyberCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Mode 2: Pocket & Bag Guard
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (pocketGuard) CyberCyanGlow else CyberSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = if (pocketGuard) CyberCyan else TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Pocket & Bag Anti-Theft",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Triggers if pulled out of pocket/bag",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
                Switch(
                    checked = pocketGuard,
                    onCheckedChange = {
                        pocketGuard = it
                        prefs.pocketGuardEnabled = it
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = CyberBackground,
                        checkedTrackColor = CyberCyan,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = CyberSurfaceVariant
                    ),
                    modifier = Modifier.testTag("toggle_pocket_guard")
                )
            }
        }

        // Mode 3: Charger Disconnect Guard
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (chargerGuard) CyberCyanGlow else CyberSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Power,
                            contentDescription = null,
                            tint = if (chargerGuard) CyberCyan else TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Charger Unplug Guard",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Alarms if charging cable is detached",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
                Switch(
                    checked = chargerGuard,
                    onCheckedChange = {
                        chargerGuard = it
                        prefs.chargerGuardEnabled = it
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = CyberBackground,
                        checkedTrackColor = CyberCyan,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = CyberSurfaceVariant
                    ),
                    modifier = Modifier.testTag("toggle_charger_guard")
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Test Alarm Trigger button
        Button(
            onClick = { viewModel.triggerManualAlarm("Test Alarm Demo") },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("quick_test_alarm_button"),
            colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
        ) {
            Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = null,
                tint = AlertRed,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "TEST SIREN & STROBE ALARM",
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
