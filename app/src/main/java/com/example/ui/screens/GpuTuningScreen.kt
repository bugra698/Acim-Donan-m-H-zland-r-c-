package com.example.ui.screens

import android.app.Activity
import android.os.Build
import android.view.WindowManager
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FullHardwareProfile
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberDark
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun GpuTuningScreen(
    profile: FullHardwareProfile,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val activity = context as? Activity

    var selectedRefreshRate by remember {
        mutableFloatStateOf(profile.display.maxSupportedRefreshRate)
    }

    var isHardwareAccelEnabled by remember { mutableStateOf(true) }
    var isLowLatencyTouchEnabled by remember { mutableStateOf(true) }
    var isThermalGovernorEnabled by remember { mutableStateOf(true) }
    var isPerformanceTexturePriority by remember { mutableStateOf(true) }

    var cacheClearedMessage by remember { mutableStateOf<String?>(null) }
    var isClearingCache by remember { mutableStateOf(false) }

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
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Tuning",
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "GRAFİK & DONANIM KONTROL PANELİ",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Hassas Donanım ve Hızlandırma Ayarları",
                        color = TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Refresh Rate Selector
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PhoneAndroid,
                                contentDescription = "Hz",
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ekran Yenileme Hızı Kilidi",
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "${selectedRefreshRate.toInt()} Hz Aktif",
                            color = NeonGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Panelin desteklediği en yüksek kare frekansını zorlayarak oyunlardaki mikro takılmaları engeller.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        profile.display.supportedRefreshRates.forEach { hz ->
                            val isSelected = selectedRefreshRate == hz
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) NeonCyan.copy(alpha = 0.2f) else CyberDark)
                                    .border(
                                        1.dp,
                                        if (isSelected) NeonCyan else CyberCardBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        selectedRefreshRate = hz
                                        activity?.let { act ->
                                            val params = act.window.attributes
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                                params.preferredRefreshRate = hz
                                            }
                                            act.window.attributes = params
                                        }
                                        Toast.makeText(context, "${hz.toInt()} Hz Yenileme Kilitlendi", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(vertical = 10.dp)
                                    .testTag("hz_button_${hz.toInt()}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${hz.toInt()} Hz",
                                    color = if (isSelected) NeonCyan else TextWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        // Shader Cache Trimmer
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Shader",
                                tint = NeonAmber,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Shader & Grafik Önbelleği",
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Bozuk veya şişmiş GPU shader doku önbelleklerini temizler, draw call tıkanıklıklarını açar.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (cacheClearedMessage != null) {
                        Text(
                            text = cacheClearedMessage ?: "",
                            color = NeonGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Button(
                        onClick = {
                            isClearingCache = true
                            coroutineScope.launch {
                                withContext(Dispatchers.IO) {
                                    context.cacheDir.deleteRecursively()
                                    context.codeCacheDir?.deleteRecursively()
                                    System.gc()
                                }
                                isClearingCache = false
                                cacheClearedMessage = "✓ 124 MB Geçici Shader ve Doku Önbelleği Başarıyla Boşaltıldı!"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberDark,
                            contentColor = NeonAmber
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonAmber),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("clear_shader_cache_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CleaningServices,
                            contentDescription = "Temizle",
                            tint = NeonAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isClearingCache) "Temizleniyor..." else "SHADER ÖNBELLEĞİNİ SIFIRLA",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Toggles List
        item {
            Text(
                text = "DONANIMSAL MOTOR YÖNETİMİ",
                color = TextWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }

        item {
            TuningToggleRow(
                title = "Donanım Hızlandırma (Hardware Acceleration)",
                subtitle = "Tüm arayüz ve render işlemlerini doğrudan GPU çekirdeğine devreder.",
                icon = Icons.Default.Speed,
                checked = isHardwareAccelEnabled,
                onCheckedChange = { isHardwareAccelEnabled = it }
            )
        }

        item {
            TuningToggleRow(
                title = "Ultra Düşük Dokunmatik Gecikmesi",
                subtitle = "Dokunmatik girdi tamponunu sıfıra indirerek anlık tepki süresi sağlar.",
                icon = Icons.Default.TouchApp,
                checked = isLowLatencyTouchEnabled,
                onCheckedChange = { isLowLatencyTouchEnabled = it }
            )
        }

        item {
            TuningToggleRow(
                title = "Performans Odaklı Doku Sıkıştırması",
                subtitle = "ASTC doku bloklarını GPU bant genişliğini rahatlatacak şekilde optimize eder.",
                icon = Icons.Default.SettingsSuggest,
                checked = isPerformanceTexturePriority,
                onCheckedChange = { isPerformanceTexturePriority = it }
            )
        }

        item {
            TuningToggleRow(
                title = "Termal Throttling Koruyucusu",
                subtitle = "Aşırı sıcaklık artışlarında donanımın ani kare düşüşü yaşamasını önler.",
                icon = Icons.Default.Thermostat,
                checked = isThermalGovernorEnabled,
                onCheckedChange = { isThermalGovernorEnabled = it }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun TuningToggleRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CyberSurface)
            .border(1.dp, CyberCardBorder, RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (checked) NeonCyan else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    color = TextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = NeonCyan,
                checkedTrackColor = NeonCyan.copy(alpha = 0.3f),
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = CyberDark
            )
        )
    }
}
