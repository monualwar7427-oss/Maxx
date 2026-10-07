package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.roundToInt

@Composable
fun NumericPinKeypad(
    currentPin: String,
    onPinDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(isError) {
        if (isError) {
            for (i in 0..2) {
                shakeOffset.animateTo(20f, tween(50))
                shakeOffset.animateTo(-20f, tween(50))
            }
            shakeOffset.animateTo(0f, tween(50))
        }
    }

    Column(
        modifier = modifier
            .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // PIN indicator dots (4 digits)
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(bottom = 28.dp)
        ) {
            for (i in 0 until 4) {
                val filled = i < currentPin.length
                val dotColor = when {
                    isError -> AlertRed
                    filled -> CyberCyan
                    else -> CyberSurfaceVariant
                }
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                        .border(
                            1.5.dp,
                            if (filled) CyberCyan else CyberBorder,
                            CircleShape
                        )
                )
            }
        }

        // Keypad Grid (1-9, Clear, 0, Backspace)
        val keys = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("C", "0", "DEL")
        )

        keys.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { key ->
                    when (key) {
                        "DEL" -> {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = ripple(bounded = true, color = CyberCyan)
                                    ) { onBackspace() }
                                    .testTag("pin_keypad_del"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                                    contentDescription = "Backspace",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        "C" -> {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = ripple(bounded = true, color = AlertRed)
                                    ) { onClear() }
                                    .testTag("pin_keypad_clear"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "CLR",
                                    color = TextSecondary,
                                    fontSize = 15.dp.value.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        else -> {
                            Surface(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, CyberBorder, CircleShape)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = ripple(bounded = true, color = CyberCyan)
                                    ) {
                                        if (currentPin.length < 4) {
                                            onPinDigit(key)
                                        }
                                    }
                                    .testTag("pin_keypad_key_$key"),
                                shape = CircleShape,
                                color = CyberSurfaceVariant
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = key,
                                        color = TextPrimary,
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
