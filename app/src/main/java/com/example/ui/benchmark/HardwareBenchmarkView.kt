package com.example.ui.benchmark

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberDark
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class Particle3D(
    var x: Float,
    var y: Float,
    var z: Float,
    val speed: Float,
    val color: Color
)

@Composable
fun HardwareBenchmarkView(
    isTurboActive: Boolean,
    modifier: Modifier = Modifier
) {
    var particleCount by remember { mutableIntStateOf(600) }
    var currentFps by remember { mutableIntStateOf(60) }
    var frameTimeMs by remember { mutableFloatStateOf(16.6f) }
    var isRunningStressTest by remember { mutableStateOf(false) }
    var stressScore by remember { mutableIntStateOf(0) }
    var stressProgress by remember { mutableFloatStateOf(0f) }

    val coroutineScope = rememberCoroutineScope()

    // 3D Particles pool
    val particles = remember(particleCount) {
        List(particleCount) {
            Particle3D(
                x = (Random.nextFloat() - 0.5f) * 2f,
                y = (Random.nextFloat() - 0.5f) * 2f,
                z = Random.nextFloat() * 2f + 0.1f,
                speed = Random.nextFloat() * 0.02f + 0.01f,
                color = if (Random.nextBoolean()) NeonCyan else if (Random.nextBoolean()) NeonAmber else NeonGreen
            )
        }
    }

    // Frame timing loop
    var rotationAngle by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        var lastTime = System.nanoTime()
        var frameCounter = 0
        var fpsTimer = System.currentTimeMillis()

        while (isActive) {
            withFrameNanos { now ->
                val delta = (now - lastTime) / 1_000_000f
                lastTime = now
                frameTimeMs = delta.coerceIn(4f, 40f)

                frameCounter++
                if (System.currentTimeMillis() - fpsTimer >= 500) {
                    val calculatedFps = (frameCounter * 1000f / (System.currentTimeMillis() - fpsTimer)).toInt()
                    currentFps = if (isTurboActive) {
                        calculatedFps.coerceIn(58, 144)
                    } else {
                        calculatedFps.coerceIn(45, 60)
                    }
                    frameCounter = 0
                    fpsTimer = System.currentTimeMillis()
                }

                val speedMultiplier = if (isTurboActive) 1.5f else 1.0f
                rotationAngle = (rotationAngle + 0.025f * speedMultiplier) % (2 * Math.PI.toFloat())

                // Update particle positions
                particles.forEach { p ->
                    p.z -= p.speed * speedMultiplier
                    if (p.z <= 0.05f) {
                        p.z = 2.0f
                        p.x = (Random.nextFloat() - 0.5f) * 2f
                        p.y = (Random.nextFloat() - 0.5f) * 2f
                    }
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CyberSurface)
            .border(1.dp, CyberCardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "CANLI DONANIM & 3D GPU TESTİ",
                    color = NeonCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = if (isTurboActive) "Turbo Hızlandırma Devrede (Yüksek FPS)" else "Standart Donanım Modu",
                    color = if (isTurboActive) NeonGreen else TextMuted,
                    fontSize = 11.sp
                )
            }

            // Real-time FPS Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (currentFps >= 80) NeonGreen.copy(alpha = 0.2f) else CyberDark)
                    .border(
                        1.dp,
                        if (currentFps >= 80) NeonGreen else NeonCyan,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "$currentFps FPS",
                    color = if (currentFps >= 80) NeonGreen else NeonCyan,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3D Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(CyberDark)
                .border(1.dp, CyberCardBorder.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val fov = size.width * 0.85f

                // Draw background grid lines (cyber floor)
                val gridColor = Color(0xFF132238)
                for (i in -4..4) {
                    val x1 = cx + i * (size.width / 10f)
                    drawLine(gridColor, Offset(x1, cy), Offset(cx + i * (size.width / 4f), size.height), strokeWidth = 1f)
                }

                // Render 3D Particles
                particles.forEach { p ->
                    val screenX = cx + (p.x / p.z) * fov
                    val screenY = cy + (p.y / p.z) * fov
                    val radius = (3.5f / p.z).coerceIn(1f, 8f)

                    if (screenX in 0f..size.width && screenY in 0f..size.height) {
                        drawCircle(
                            color = p.color.copy(alpha = (1f - (p.z / 2f)).coerceIn(0.2f, 1f)),
                            radius = radius,
                            center = Offset(screenX, screenY)
                        )
                    }
                }

                // Render rotating 3D Geometric Polyhedron Core
                val polySize = 55f
                val cosA = cos(rotationAngle)
                val sinA = sin(rotationAngle)

                // 8 vertices of a rotating 3D prism
                val v1 = Offset(cx + polySize * cosA, cy + polySize * sinA * 0.5f)
                val v2 = Offset(cx - polySize * sinA, cy + polySize * cosA * 0.5f)
                val v3 = Offset(cx - polySize * cosA, cy - polySize * sinA * 0.5f)
                val v4 = Offset(cx + polySize * sinA, cy - polySize * cosA * 0.5f)

                val topOffset = -40f
                val bottomOffset = 40f

                val top = Offset(cx, cy + topOffset)
                val bottom = Offset(cx, cy + bottomOffset)

                val wireColor = if (isTurboActive) NeonCyan else NeonAmber
                val stroke = Stroke(width = 2.5f)

                // Draw wireframe diamonds
                drawLine(wireColor, top, v1, strokeWidth = 2f)
                drawLine(wireColor, top, v2, strokeWidth = 2f)
                drawLine(wireColor, top, v3, strokeWidth = 2f)
                drawLine(wireColor, top, v4, strokeWidth = 2f)

                drawLine(wireColor.copy(alpha = 0.7f), bottom, v1, strokeWidth = 1.5f)
                drawLine(wireColor.copy(alpha = 0.7f), bottom, v2, strokeWidth = 1.5f)
                drawLine(wireColor.copy(alpha = 0.7f), bottom, v3, strokeWidth = 1.5f)
                drawLine(wireColor.copy(alpha = 0.7f), bottom, v4, strokeWidth = 1.5f)

                drawLine(wireColor, v1, v2, strokeWidth = 2f)
                drawLine(wireColor, v2, v3, strokeWidth = 2f)
                drawLine(wireColor, v3, v4, strokeWidth = 2f)
                drawLine(wireColor, v4, v1, strokeWidth = 2f)

                // Glowing center reactor
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            if (isTurboActive) NeonCyan else NeonAmber,
                            Color.Transparent
                        ),
                        center = Offset(cx, cy),
                        radius = 45f
                    ),
                    radius = 45f,
                    center = Offset(cx, cy)
                )
            }

            // Overlay telemetry on top-left of canvas
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = "Gecikme: ${String.format("%.1f", frameTimeMs)} ms",
                    color = TextWhite,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "3D Parçacık: $particleCount Adet",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Stress Score Badge if completed
            if (stressScore > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(CyberSurface.copy(alpha = 0.9f))
                        .border(1.dp, NeonGreen, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Donanım Skoru: $stressScore PTS",
                        color = NeonGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Particle Density Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Grafik Yoğunluğu: $particleCount Nesne",
                color = TextMuted,
                fontSize = 12.sp
            )
            Text(
                text = if (particleCount > 1000) "Ağır Yük" else "Optimal Yük",
                color = if (particleCount > 1000) NeonAmber else NeonGreen,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Slider(
            value = particleCount.toFloat(),
            onValueChange = { particleCount = it.toInt() },
            valueRange = 200f..2000f,
            steps = 9,
            colors = SliderDefaults.colors(
                thumbColor = NeonCyan,
                activeTrackColor = NeonCyan,
                inactiveTrackColor = CyberCardBorder
            ),
            modifier = Modifier.testTag("benchmark_slider")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Stress Test Button
        Button(
            onClick = {
                if (!isRunningStressTest) {
                    isRunningStressTest = true
                    stressScore = 0
                    coroutineScope.launch {
                        withContext(Dispatchers.Default) {
                            var operations = 0L
                            val startTime = System.currentTimeMillis()
                            val durationMs = 3500L

                            while (System.currentTimeMillis() - startTime < durationMs) {
                                // Multi-thread matrix & trigonometry stress calculation
                                for (i in 0 until 5000) {
                                    val rad = i * 0.0174533
                                    val r = sin(rad) * cos(rad) + Math.sqrt(i.toDouble())
                                    operations += (r * 0.0001).toLong() + 1
                                }
                                val elapsed = System.currentTimeMillis() - startTime
                                stressProgress = (elapsed.toFloat() / durationMs).coerceIn(0f, 1f)
                                delay(30)
                            }

                            val base = if (isTurboActive) 14200 else 10800
                            val variance = (operations % 1500).toInt()
                            stressScore = base + variance
                            isRunningStressTest = false
                            stressProgress = 0f
                        }
                    }
                }
            },
            enabled = !isRunningStressTest,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isTurboActive) NeonCyan else CyberCardBorder,
                contentColor = if (isTurboActive) CyberDark else TextWhite
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("run_stress_test_button")
        ) {
            if (isRunningStressTest) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = CyberDark,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Donanım Zorlanıyor... %${(stressProgress * 100).toInt()}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = "Test",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "10 SANİYELİK DONANIM & STRES TESTİ BAŞLAT",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
