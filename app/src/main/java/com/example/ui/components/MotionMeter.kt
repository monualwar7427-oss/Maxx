package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.ShieldAmber
import com.example.ui.theme.ShieldGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun MotionMeter(
    currentMotion: Float,
    threshold: Float,
    modifier: Modifier = Modifier
) {
    val maxScale = (threshold * 2.2f).coerceAtLeast(3f)
    val fraction = (currentMotion / maxScale).coerceIn(0f, 1f)
    val animatedFraction by animateFloatAsState(targetValue = fraction, animationSpec = tween(80), label = "motion")

    val isDanger = currentMotion >= threshold
    val barColor by animateColorAsState(
        targetValue = when {
            isDanger -> AlertRed
            currentMotion > threshold * 0.6f -> ShieldAmber
            else -> ShieldGreen
        },
        label = "color"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MOTION SENSOR TELEMETRY",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
            Text(
                text = String.format(Locale.US, "%.2f / %.1f m/s²", currentMotion, threshold),
                color = if (isDanger) AlertRed else CyberCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Progress bar with threshold indicator mark
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(Color(0xFF0F172A))
                .border(1.dp, CyberBorder, RoundedCornerShape(7.dp))
        ) {
            // Fill
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedFraction)
                    .clip(RoundedCornerShape(7.dp))
                    .background(barColor)
            )

            // Threshold marker tick
            val markerOffsetFraction = (threshold / maxScale).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(2.dp)
                    .fillMaxWidth(markerOffsetFraction)
                    .align(Alignment.CenterStart)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(2.dp)
                        .background(Color.White)
                        .align(Alignment.CenterEnd)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "CALM (0.0)",
                color = TextMuted,
                fontSize = 10.sp
            )
            Text(
                text = "TRIGGER THRESHOLD",
                color = TextMuted,
                fontSize = 10.sp
            )
            Text(
                text = "MAX",
                color = TextMuted,
                fontSize = 10.sp
            )
        }
    }
}
