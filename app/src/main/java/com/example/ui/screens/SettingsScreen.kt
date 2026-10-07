package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.TouchGuardViewModel
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    viewModel: TouchGuardViewModel,
    modifier: Modifier = Modifier
) {
    val prefs = viewModel.prefs

    var ownerName by remember { mutableStateOf(prefs.ownerName) }
    var pinCode by remember { mutableStateOf(prefs.pinCode) }
    var sensitivityLevel by remember { mutableIntStateOf(prefs.sensitivityLevel) }
    var armingDelay by remember { mutableIntStateOf(prefs.armingDelaySeconds) }
    var sirenTone by remember { mutableStateOf(prefs.sirenTone) }
    var voiceAlert by remember { mutableStateOf(prefs.voiceAlertEnabled) }
    var flashlightStrobe by remember { mutableStateOf(prefs.flashlightStrobeEnabled) }
    var vibration by remember { mutableStateOf(prefs.vibrationEnabled) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "CONFIGURATION",
            color = CyberCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.5.sp
        )
        Text(
            text = "TouchGuard & Max Settings",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Owner Name & Customization
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Owner Name (Max calls you by this)",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = ownerName,
                    onValueChange = {
                        ownerName = it
                        prefs.ownerName = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("owner_name_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Security PIN Code
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "4-Digit Security Disarm PIN",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = pinCode,
                    onValueChange = {
                        if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                            pinCode = it
                            if (it.length == 4) prefs.pinCode = it
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pin_code_input"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Sensitivity Selector
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Motion Sensor Sensitivity",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val levels = listOf(
                        1 to "High (Gentle Touch)",
                        2 to "Normal (Desk Mode)",
                        3 to "Low (Firm Move)"
                    )
                    levels.forEach { (lvl, title) ->
                        val isSelected = sensitivityLevel == lvl
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    sensitivityLevel = lvl
                                    prefs.sensitivityLevel = lvl
                                },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) CyberCyan else CyberSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) CyberCyan else CyberBorder
                            )
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) CyberBackground else TextSecondary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Arming Countdown Delay
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Arming Countdown Delay",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(3, 5, 10).forEach { sec ->
                        val isSelected = armingDelay == sec
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    armingDelay = sec
                                    prefs.armingDelaySeconds = sec
                                },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) CyberCyan else CyberSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) CyberCyan else CyberBorder
                            )
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$sec Seconds",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) CyberBackground else TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Siren Tone Selection
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Alarm Sound Tone",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                val tones = listOf(
                    "POLICE_SIREN" to "Police Siren",
                    "TACTICAL_WARBLE" to "Tactical Warble",
                    "PULSE_ALARM" to "Pulse Strobe",
                    "AIR_RAID" to "Air Raid Wail"
                )

                tones.forEach { (id, title) ->
                    val isSelected = sirenTone == id
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                sirenTone = id
                                prefs.sirenTone = id
                            },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) CyberCyan.copy(alpha = 0.15f) else CyberSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) CyberCyan else CyberBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) CyberCyan else TextPrimary
                            )
                            if (isSelected) {
                                Text(
                                    text = "SELECTED",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = CyberCyan
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Toggles: Voice Alert, Flashlight Strobe, Vibration
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Voice Alert
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Max Voice Alert",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "\"Alert! Aapka phone kisi ne chhua hai!\"",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                    Switch(
                        checked = voiceAlert,
                        onCheckedChange = {
                            voiceAlert = it
                            prefs.voiceAlertEnabled = it
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyberBackground,
                            checkedTrackColor = CyberCyan
                        )
                    )
                }

                // Flashlight Strobe
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Flashlight Strobe",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Blink camera torch at high frequency",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                    Switch(
                        checked = flashlightStrobe,
                        onCheckedChange = {
                            flashlightStrobe = it
                            prefs.flashlightStrobeEnabled = it
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyberBackground,
                            checkedTrackColor = CyberCyan
                        )
                    )
                }

                // Vibration
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Intense Vibration",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Heavy pulse vibration during alarm",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                    Switch(
                        checked = vibration,
                        onCheckedChange = {
                            vibration = it
                            prefs.vibrationEnabled = it
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyberBackground,
                            checkedTrackColor = CyberCyan
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
