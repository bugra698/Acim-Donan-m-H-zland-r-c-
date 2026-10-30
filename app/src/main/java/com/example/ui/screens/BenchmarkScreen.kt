package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FullHardwareProfile
import com.example.data.model.HardwareDecision
import com.example.domain.OptimizationState
import com.example.ui.benchmark.HardwareBenchmarkView
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberDark
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.TierSColor

@Composable
fun BenchmarkScreen(
    profile: FullHardwareProfile,
    decision: HardwareDecision,
    optimizationState: OptimizationState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(NeonCyan.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Benchmark",
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "GERÇEK ZAMANLI GPU STRES & FPS TESTİ",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Donanım Kararlılığı ve Grafik Çizim Hızı",
                        color = TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 3D Canvas Interactive Benchmark Component
        item {
            HardwareBenchmarkView(
                isTurboActive = optimizationState.isBoostActive
            )
        }

        // Hardware Benchmark Comparison
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "Karşılaştırma",
                            tint = NeonGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DONANIM SEGMENT KARŞILAŞTIRMASI",
                            color = NeonGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    BenchmarkScoreBar(
                        label = "Cihazınız (${profile.gpu.renderer.take(18)})",
                        score = decision.score,
                        maxScore = 100,
                        color = NeonCyan,
                        isCurrentDevice = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    BenchmarkScoreBar(
                        label = "Tier S+ Ultra Flagship SoC",
                        score = 98,
                        maxScore = 100,
                        color = TierSColor
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    BenchmarkScoreBar(
                        label = "Tier A Flagship Gaming SoC",
                        score = 86,
                        maxScore = 100,
                        color = NeonPurple
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    BenchmarkScoreBar(
                        label = "Tier B Performans Orta Segment",
                        score = 72,
                        maxScore = 100,
                        color = NeonGreen
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    BenchmarkScoreBar(
                        label = "Tier C Standart Giriş Segmenti",
                        score = 52,
                        maxScore = 100,
                        color = NeonAmber
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun BenchmarkScoreBar(
    label: String,
    score: Int,
    maxScore: Int,
    color: Color,
    isCurrentDevice: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                color = if (isCurrentDevice) NeonCyan else TextWhite,
                fontSize = 12.sp,
                fontWeight = if (isCurrentDevice) FontWeight.Bold else FontWeight.Normal
            )
            Text(
                text = "$score Puan",
                color = color,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(CyberDark)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = (score.toFloat() / maxScore).coerceIn(0.1f, 1f))
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
    }
}
