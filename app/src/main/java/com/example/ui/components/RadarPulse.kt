package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyberCyan
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarPulse(
    isArmed: Boolean,
    isAlarm: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar_anim")

    val pulse1 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse1"
    )

    val pulse2 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, delayMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse2"
    )

    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )

    val activeColor = if (isAlarm) AlertRed else CyberCyan

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val maxRadius = (size.minDimension / 2) * 0.95f

            // Concentric radar guide circles
            val ringCount = 3
            for (i in 1..ringCount) {
                drawCircle(
                    color = activeColor.copy(alpha = 0.12f),
                    radius = maxRadius * (i.toFloat() / ringCount),
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }

            if (isArmed || isAlarm) {
                // Expanding ripple waves
                val alpha1 = (1f - pulse1).coerceIn(0f, 0.7f)
                drawCircle(
                    color = activeColor.copy(alpha = alpha1),
                    radius = maxRadius * pulse1,
                    center = center,
                    style = Stroke(width = 2.dp.toPx())
                )

                val alpha2 = (1f - pulse2).coerceIn(0f, 0.7f)
                drawCircle(
                    color = activeColor.copy(alpha = alpha2),
                    radius = maxRadius * pulse2,
                    center = center,
                    style = Stroke(width = 2.dp.toPx())
                )

                // Rotating radar beam
                val rad = Math.toRadians(sweepAngle.toDouble())
                val beamEnd = Offset(
                    center.x + (maxRadius * cos(rad)).toFloat(),
                    center.y + (maxRadius * sin(rad)).toFloat()
                )
                drawLine(
                    brush = Brush.radialGradient(
                        colors = listOf(activeColor.copy(alpha = 0.8f), Color.Transparent),
                        center = center,
                        radius = maxRadius
                    ),
                    start = center,
                    end = beamEnd,
                    strokeWidth = 2.dp.toPx()
                )
            }
        }

        content()
    }
}
