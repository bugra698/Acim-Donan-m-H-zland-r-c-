package com.example.ui.screens

import android.app.Activity
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BottleneckSeverity
import com.example.data.model.FullHardwareProfile
import com.example.data.model.HardwareDecision
import com.example.data.model.HardwareTier
import com.example.data.model.OptimizationProfile
import com.example.domain.OptimizationEngine
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberDark
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.TierSColor
import kotlinx.coroutines.launch

@Composable
fun DecisionReportScreen(
    profile: FullHardwareProfile,
    decision: HardwareDecision,
    optimizationEngine: OptimizationEngine,
    onOptimizationApplied: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val activity = context as? Activity

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            // Header
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
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Karar",
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "DONANIM ANALİZİ & KARAR RAPORU",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${profile.os.deviceBrand} ${profile.os.deviceModel}",
                        color = TextWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // Score & Tier Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, TierSColor.copy(alpha = 0.8f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "HESAPLANAN DONANIM SEVİYESİ",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = decision.tier.label,
                                color = TierSColor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        // Score Badge
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(CyberDark)
                                .border(2.dp, TierSColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${decision.score}",
                                    color = TextWhite,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "/100",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = CyberCardBorder)
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = decision.verdictSummary,
                        color = TextWhite,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SpecSummaryPill(
                            label = "Oyun Gücü",
                            value = "%${decision.gamingCapabilityScore}",
                            color = NeonGreen
                        )
                        SpecSummaryPill(
                            label = "Hedef FPS",
                            value = "${decision.expectedMaxFpsTarget} FPS",
                            color = NeonCyan
                        )
                        SpecSummaryPill(
                            label = "Işın İzleme",
                            value = if (decision.rayTracingStatus.contains("Donanımsal")) "Destekli" else "Yazılımsal",
                            color = NeonAmber
                        )
                    }
                }
            }
        }

        // Action: Optimize to this Decision Button
        item {
            Button(
                onClick = {
                    coroutineScope.launch {
                        optimizationEngine.runOptimization(
                            profile = OptimizationProfile.TURBO_GAMING,
                            activity = activity
                        )
                        onOptimizationApplied()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan,
                    contentColor = CyberDark
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("apply_decision_optimization_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Optimize",
                    tint = CyberDark
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "BU KARARA GÖRE DONANIMI OPTİMİZE ET",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Detailed Hardware Specs: CPU, GPU, OS
        item {
            Text(
                text = "AYRINTILI BİLEŞEN TEŞHİSİ",
                color = TextWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }

        // 1. Processor (CPU) Card
        item {
            HardwareComponentCard(
                title = "İşlemci (CPU) Mimarisi",
                icon = Icons.Default.Memory,
                accentColor = NeonCyan,
                specs = listOf(
                    "İşlemci Modeli" to profile.cpu.modelName,
                    "Çekirdek Sayısı" to "${profile.cpu.coreCount} Çekirdek (Multi-Thread)",
                    "Mimari / ABI" to profile.cpu.architecture,
                    "Maks Saat Frekansı" to "~${profile.cpu.maxFreqGhz} GHz",
                    "Donanım Kodu" to profile.cpu.hardware,
                    "Yonga Seti Board" to profile.cpu.board
                )
            )
        }

        // 2. Graphics (GPU) Card
        item {
            HardwareComponentCard(
                title = "Grafik İşlemci (GPU) & Vulkan",
                icon = Icons.Default.Speed,
                accentColor = NeonGreen,
                specs = listOf(
                    "GPU Modeli" to profile.gpu.renderer,
                    "GPU Satıcısı" to profile.gpu.vendor,
                    "OpenGL ES Sürümü" to profile.gpu.version,
                    "Vulkan Desteği" to profile.gpu.vulkanVersionString,
                    "Maks Doku Boyutu" to "${profile.gpu.maxTextureSize} x ${profile.gpu.maxTextureSize} px",
                    "ASTC Sıkıştırma" to if (profile.gpu.astcSupported) "Donanımsal Destekli" else "Desteklenmiyor",
                    "GPU Uzantı Sayısı" to "${profile.gpu.extensionsCount} GL Uzantısı"
                )
            )
        }

        // 3. Android OS & Kernel Card
        item {
            HardwareComponentCard(
                title = "Android Sürümü & Sistem",
                icon = Icons.Default.Android,
                accentColor = NeonAmber,
                specs = listOf(
                    "Android Sürümü" to "Android ${profile.os.androidRelease} (API ${profile.os.apiLevel})",
                    "Güvenlik Yaması" to profile.os.securityPatch,
                    "Linux Kernel" to profile.os.kernelVersion,
                    "Ekran Çözünürlüğü" to "${profile.display.resolution} (${profile.display.densityDpi} DPI)",
                    "Ekran Yenileme" to "${profile.display.currentRefreshRate.toInt()} Hz (Maks: ${profile.display.maxSupportedRefreshRate.toInt()} Hz)",
                    "HDR Desteği" to if (profile.display.isHdrSupported) "HDR10 / Wide Color Gamut" else "Standart SDR",
                    "Toplam RAM" to "${profile.memory.totalRamMb} MB (~${(profile.memory.totalRamMb / 1024.0).toInt()} GB)",
                    "Dahili Depolama" to "${profile.memory.freeStorageGb} GB Boş / ${profile.memory.totalStorageGb} GB Toplam"
                )
            )
        }

        // Bottlenecks & Warnings
        if (decision.detectedBottlenecks.isNotEmpty()) {
            item {
                Text(
                    text = "TESPİT EDİLEN DONANIM DARBOĞAZLARI",
                    color = NeonRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    decision.detectedBottlenecks.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(CyberSurface)
                                .border(
                                    1.dp,
                                    if (item.severity == BottleneckSeverity.HIGH) NeonRed.copy(alpha = 0.6f) else NeonAmber.copy(alpha = 0.6f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Uyarı",
                                tint = if (item.severity == BottleneckSeverity.HIGH) NeonRed else NeonAmber,
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = item.title,
                                    color = TextWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.description,
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Personalized Strategy Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceVariant),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Plan",
                            tint = NeonPurple,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "BU ÇİPE ÖZEL OPTİMİZASYON PLANI",
                            color = NeonPurple,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = decision.recommendedStrategy,
                        color = TextWhite,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
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
fun HardwareComponentCard(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    specs: List<Pair<String, String>>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CyberSurface)
            .border(1.dp, CyberCardBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = accentColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title.uppercase(),
                color = accentColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        specs.forEachIndexed { index, (label, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    color = TextMuted,
                    fontSize = 12.sp
                )
                Text(
                    text = value,
                    color = TextWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace
                )
            }
            if (index < specs.size - 1) {
                HorizontalDivider(color = CyberCardBorder.copy(alpha = 0.5f), thickness = 0.5.dp)
            }
        }
    }
}

@Composable
fun SpecSummaryPill(
    label: String,
    value: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CyberDark)
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, color = TextMuted, fontSize = 10.sp)
            Text(
                text = value,
                color = color,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
