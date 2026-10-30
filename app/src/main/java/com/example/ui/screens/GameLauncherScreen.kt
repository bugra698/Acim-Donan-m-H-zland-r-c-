package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FullHardwareProfile
import com.example.data.model.HardwareDecision
import com.example.data.model.InstalledGameApp
import com.example.data.model.OptimizationProfile
import com.example.domain.OptimizationEngine
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberDark
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.launch

@Composable
fun GameLauncherScreen(
    profile: FullHardwareProfile,
    decision: HardwareDecision,
    optimizationEngine: OptimizationEngine,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val activity = context as? Activity

    // Scan installed applications that have launcher activities
    val appsList = remember {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolved = pm.queryIntentActivities(intent, 0)
        val list = mutableListOf<InstalledGameApp>()

        resolved.forEach { resolveInfo ->
            val pkg = resolveInfo.activityInfo.packageName
            if (pkg != context.packageName) {
                val label = resolveInfo.loadLabel(pm).toString()
                val isGame = try {
                    val appInfo = pm.getApplicationInfo(pkg, 0)
                    (appInfo.flags and ApplicationInfo.FLAG_IS_GAME) != 0 ||
                            (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O &&
                                    appInfo.category == ApplicationInfo.CATEGORY_GAME) ||
                            pkg.contains("game", ignoreCase = true)
                } catch (_: Exception) { false }

                list.add(
                    InstalledGameApp(
                        packageName = pkg,
                        appName = label,
                        isGame = isGame
                    )
                )
            }
        }
        list.sortedByDescending { it.isGame }
    }

    // Popular game preset tuning guidelines for this specific phone
    val gamePresets = remember(profile.gpu.renderer) {
        listOf(
            "PUBG Mobile" to if (decision.score >= 80) "90/120 FPS Extreme HDR | Adreno/Mali Vulkan Modu" else "60 FPS Sabit | Düzgün Grafik",
            "Genshin Impact" to if (decision.score >= 85) "60 FPS Yüksek Grafik | ASTC Doku Tamponu Açık" else "45-60 FPS Orta Ayarlar | Shader Temizliği",
            "Call of Duty: Warzone" to if (decision.score >= 80) "120 FPS Düşük Gecikme | Maksimum Dokunmatik Hızı" else "60 FPS Dengeli | Pil Korumalı",
            "Asphalt Legends Unite" to "60 FPS 1080p | Donanımsal Hareket İzi ve Yansıma",
            "Roblox & Minecraft" to "Maksimum Görüş Mesafesi (Render Distance) | 120 FPS"
        )
    }

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
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = "Oyunlar",
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "OYUN & UYGULAMA HIZLANDIRICI",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Donanımı Boşalt & Oyunu Başlat",
                        color = TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Popular Game Presets for this GPU
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonAmber.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VideogameAsset,
                            contentDescription = "Preset",
                            tint = NeonAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "BU GPU İÇİN EN İYİ OYUN AYARLARI",
                            color = NeonAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    gamePresets.forEach { (game, recommendation) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = game,
                                color = TextWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = recommendation,
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // Installed Apps / Games Header
        item {
            Text(
                text = "YÜKLÜ UYGULAMALAR (${appsList.size})",
                color = TextWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }

        if (appsList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Yüklü ek uygulama bulunamadı.",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(appsList.take(20)) { app ->
                InstalledAppBoostCard(
                    app = app,
                    onBoostAndLaunch = {
                        coroutineScope.launch {
                            optimizationEngine.runOptimization(
                                profile = OptimizationProfile.TURBO_GAMING,
                                activity = activity
                            )
                            Toast.makeText(context, "${app.appName} için Turbo Boost uygulandı! Başlatılıyor...", Toast.LENGTH_SHORT).show()
                            val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                            if (launchIntent != null) {
                                context.startActivity(launchIntent)
                            }
                        }
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun InstalledAppBoostCard(
    app: InstalledGameApp,
    onBoostAndLaunch: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CyberSurface)
            .border(1.dp, CyberCardBorder, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (app.isGame) NeonGreen.copy(alpha = 0.2f) else CyberDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (app.isGame) Icons.Default.SportsEsports else Icons.Default.RocketLaunch,
                    contentDescription = app.appName,
                    tint = if (app.isGame) NeonGreen else NeonCyan,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = app.appName,
                    color = TextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (app.isGame) "3D Oyun Motoru" else "Uygulama",
                    color = if (app.isGame) NeonGreen else TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        Button(
            onClick = onBoostAndLaunch,
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonCyan,
                contentColor = CyberDark
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("boost_launch_${app.packageName.replace(".", "_")}")
        ) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = "Boost",
                tint = CyberDark,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "HIZLANDIR & AÇ",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
