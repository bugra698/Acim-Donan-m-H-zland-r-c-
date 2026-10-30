package com.example.domain

import com.example.data.model.BottleneckItem
import com.example.data.model.BottleneckSeverity
import com.example.data.model.FullHardwareProfile
import com.example.data.model.HardwareDecision
import com.example.data.model.HardwareTier

class HardwareDecisionEngine {

    fun analyzeAndDecide(profile: FullHardwareProfile): HardwareDecision {
        val cpu = profile.cpu
        val gpu = profile.gpu
        val display = profile.display
        val memory = profile.memory
        val thermal = profile.thermal
        val os = profile.os

        // Calculate Hardware Tier & Score
        var baseScore = 60
        val bottlenecks = mutableListOf<BottleneckItem>()

        // 1. GPU Evaluation
        val rendererLower = gpu.renderer.lowercase()
        val isAdreno = rendererLower.contains("adreno")
        val isMali = rendererLower.contains("mali") || rendererLower.contains("immortalis")
        val isTensorOrApple = rendererLower.contains("tensor") || rendererLower.contains("apple")
        val isFlagshipGpu = rendererLower.contains("730") || rendererLower.contains("740") ||
                rendererLower.contains("750") || rendererLower.contains("830") ||
                rendererLower.contains("g715") || rendererLower.contains("g720") ||
                rendererLower.contains("g77") || rendererLower.contains("g78")

        if (isFlagshipGpu || gpu.maxTextureSize >= 16384) {
            baseScore += 18
        } else if (isAdreno || isMali || gpu.maxTextureSize >= 8192) {
            baseScore += 12
        } else {
            baseScore += 5
        }

        if (gpu.vulkanSupported) {
            baseScore += 8
        } else {
            bottlenecks.add(
                BottleneckItem(
                    title = "Vulkan Donanım Katmanı Eksik",
                    description = "Cihazınız modern Vulkan düşük gecikmeli render API'sini tam desteklemiyor; OpenGL ES emülasyonu devrede.",
                    severity = BottleneckSeverity.HIGH
                )
            )
        }

        // 2. CPU Evaluation
        val cores = cpu.coreCount
        if (cores >= 8) {
            baseScore += 10
        } else if (cores >= 6) {
            baseScore += 6
        } else {
            baseScore += 2
            bottlenecks.add(
                BottleneckItem(
                    title = "Sınırlı Çekirdek Sayısı ($cores Çekirdek)",
                    description = "Çoklu iş parçacığı gerektiren oyun motorlarında (Unreal Engine/Unity) kare düşüşü yaşanabilir.",
                    severity = BottleneckSeverity.MEDIUM
                )
            )
        }

        // 3. RAM Evaluation
        val totalRamGb = memory.totalRamMb / 1024.0
        if (totalRamGb >= 11.5) {
            baseScore += 12
        } else if (totalRamGb >= 7.5) {
            baseScore += 9
        } else if (totalRamGb >= 5.5) {
            baseScore += 6
        } else {
            baseScore += 2
            bottlenecks.add(
                BottleneckItem(
                    title = "Düşük RAM Kapasitesi (${memory.totalRamMb} MB)",
                    description = "Arka plan işlemleri ve ağır grafik dokuları cihaz hafızasını aşırı zorlayabilir.",
                    severity = BottleneckSeverity.HIGH
                )
            )
        }

        if (memory.ramUsagePercent > 72) {
            bottlenecks.add(
                BottleneckItem(
                    title = "Aktif Bellek Baskısı (%${memory.ramUsagePercent} Dolu)",
                    description = "Kullanılabilir boş RAM ${memory.availRamMb} MB. GPU framebuffer ve doku yüklemeleri için agresif temizlik gerekiyor.",
                    severity = BottleneckSeverity.HIGH
                )
            )
        }

        // 4. Display Refresh Rate
        val maxHz = display.maxSupportedRefreshRate
        val currentHz = display.currentRefreshRate
        if (maxHz >= 120f) {
            baseScore += 8
        } else if (maxHz >= 90f) {
            baseScore += 5
        } else {
            baseScore += 2
        }

        if (maxHz > currentHz && currentHz <= 60f) {
            bottlenecks.add(
                BottleneckItem(
                    title = "Ekran Hz Kısıtlaması Aktif (${currentHz.toInt()} Hz)",
                    description = "Paneliniz ${maxHz.toInt()} Hz yenileme desteklemesine rağmen enerji koruma nedeniyle 60 Hz'e sabitlenmiş.",
                    severity = BottleneckSeverity.MEDIUM
                )
            )
        }

        // 5. Thermal State
        if (thermal.isThrottlingRisk || thermal.temperatureC > 39f) {
            baseScore -= 6
            bottlenecks.add(
                BottleneckItem(
                    title = "Termal Isınma & Throttling Riski (${thermal.temperatureC}°C)",
                    description = "İşlemci ve GPU sıcaklığı yükseldiğinde donanım frekans kısma (Throttling) korumasına geçebilir.",
                    severity = BottleneckSeverity.HIGH
                )
            )
        }

        val finalScore = baseScore.coerceIn(35, 99)

        // Tier Determination
        val tier = when {
            finalScore >= 90 -> HardwareTier.TIER_S_PLUS
            finalScore >= 80 -> HardwareTier.TIER_A
            finalScore >= 68 -> HardwareTier.TIER_B
            finalScore >= 50 -> HardwareTier.TIER_C
            else -> HardwareTier.TIER_D
        }

        // Target FPS
        val targetFps = when {
            maxHz >= 144f && finalScore >= 85 -> 144
            maxHz >= 120f && finalScore >= 75 -> 120
            maxHz >= 90f && finalScore >= 65 -> 90
            else -> 60
        }

        // Architecture Analysis Text
        val archAnalysis = buildString {
            append("• İşlemci: ${cpu.modelName} (${cpu.coreCount} Çekirdek, ${cpu.architecture})\n")
            append("• Grafik Yonga: ${gpu.renderer} (${gpu.vendor})\n")
            append("• API & Standart: ${gpu.version} | ${gpu.vulkanVersionString}\n")
            append("• Maksimum Doku Boyutu: ${gpu.maxTextureSize}x${gpu.maxTextureSize} piksel\n")
            append("• Sistem: Android ${os.androidRelease} (API ${os.apiLevel}), Çekirdek: ${os.kernelVersion}\n")
            if (gpu.astcSupported) {
                append("• Doku Sıkıştırma: ASTC LDR Donanımsal Hızlandırma Aktif\n")
            }
            append("• Bellek Havuzu: ${memory.totalRamMb} MB Toplam, ${memory.availRamMb} MB Serbest (%${memory.ramUsagePercent} Aktif)")
        }

        // Decision Summary & Strategy
        val verdictSummary = when (tier) {
            HardwareTier.TIER_S_PLUS -> "Cihazınız olağanüstü yüksek seviye oyun ve grafik donanımına sahip. Tüm 3D yapımlar ve Vulkan motorları ultra ayarlarda çalıştırılabilir."
            HardwareTier.TIER_A -> "Cihazınız güçlü bir amiral gemisi mimarisine sahip. Küçük yazılımsal optimizasyonlar ve bellek tamponu temizliğiyle 90-120 FPS akıcılık elde edilir."
            HardwareTier.TIER_B -> "Dengeli ve yetenekli bir donanım mimarisi. Grafik yükü altında ısınmayı dengelemek ve RAM havuzunu boşaltmak en yüksek FPS kararlılığını sağlar."
            HardwareTier.TIER_C -> "Giriş-orta seviye grafik işlemcisi. Agresif arka plan temizliği, shader önbellek boşaltması ve sabit 60 FPS kilitlenmesi önerilir."
            HardwareTier.TIER_D -> "Donanım kaynakları kısıtlı. Maksimum performans için hafifletilmiş çözünürlük tamponları ve sürekli bellek optimizasyonu şart."
        }

        val recommendedStrategy = when {
            isAdreno -> "Qualcomm Adreno GPU için öneri: Adreno ASTC doku önbelleği boşaltılmalı, Vulkan Swapchain tamponu genişletilmeli ve ekran yenileme hızı ${targetFps} Hz'e kilitlenmelidir."
            isMali -> "ARM Mali GPU için öneri: Mali Tile-Based Deferred Rendering (TBDR) mimarisi bant genişliğine duyarlıdır. RAM havuzu temiz tutulmalı ve termal kontrol devrede olmalıdır."
            else -> "Genel GPU optimizasyonu: Grafik iş parçacığı önceliği URGENT_DISPLAY yapılmalı, arka plan bellek baskısı kaldırılmalı ve panel ${targetFps} Hz'e sabitlenmelidir."
        }

        return HardwareDecision(
            tier = tier,
            score = finalScore,
            verdictSummary = verdictSummary,
            architectureAnalysis = archAnalysis,
            detectedBottlenecks = bottlenecks,
            recommendedStrategy = recommendedStrategy,
            gamingCapabilityScore = (finalScore * 0.98f).toInt().coerceIn(40, 99),
            rayTracingStatus = if (isFlagshipGpu && gpu.vulkanSupported) "Donanımsal / Hibrit Destekli" else "Yazılımsal Emülasyon",
            expectedMaxFpsTarget = targetFps
        )
    }
}
