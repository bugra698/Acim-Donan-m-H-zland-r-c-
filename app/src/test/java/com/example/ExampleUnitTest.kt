package com.example

import com.example.data.model.CpuInfo
import com.example.data.model.DisplayInfo
import com.example.data.model.FullHardwareProfile
import com.example.data.model.GpuInfo
import com.example.data.model.HardwareTier
import com.example.data.model.MemoryStorageInfo
import com.example.data.model.OsVersionInfo
import com.example.data.model.ThermalBatteryInfo
import com.example.domain.HardwareDecisionEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testFlagshipHardwareEvaluation() {
        val engine = HardwareDecisionEngine()
        val flagshipProfile = FullHardwareProfile(
            cpu = CpuInfo("Snapdragon 8 Gen 3", 8, "arm64-v8a", "qcom", "kalama", 3.3f, 40),
            gpu = GpuInfo("Adreno (TM) 750", "Qualcomm", "OpenGL ES 3.2", 16384, true, "Vulkan 1.3", true, 180),
            display = DisplayInfo("1440 x 3120", 510, 120.0f, 120.0f, listOf(60f, 90f, 120f), true),
            memory = MemoryStorageInfo(12288, 7168, 5120, 41, false, 512, 380),
            thermal = ThermalBatteryInfo(32.5f, 90, 4150, "Optimal", 0, false),
            os = OsVersionInfo("14", 34, "2024-10-01", "6.1.45-android", "Samsung", "Galaxy S24 Ultra")
        )

        val decision = engine.analyzeAndDecide(flagshipProfile)
        assertEquals(HardwareTier.TIER_S_PLUS, decision.tier)
        assertTrue(decision.score >= 90)
        assertTrue(decision.recommendedStrategy.contains("Adreno"))
    }

    @Test
    fun testMidrangeHardwareEvaluation() {
        val engine = HardwareDecisionEngine()
        val midProfile = FullHardwareProfile(
            cpu = CpuInfo("MediaTek Helio G99", 8, "arm64-v8a", "mt6789", "k6789v1", 2.2f, 65),
            gpu = GpuInfo("Mali-G57 MC2", "ARM", "OpenGL ES 3.1", 4096, false, "Vulkan Yok", false, 60),
            display = DisplayInfo("1080 x 2400", 395, 60.0f, 90.0f, listOf(60f, 90f), false),
            memory = MemoryStorageInfo(4096, 1200, 2896, 71, false, 64, 20),
            thermal = ThermalBatteryInfo(36.0f, 65, 3950, "Optimal", 0, false),
            os = OsVersionInfo("13", 33, "2024-05-01", "5.10.100", "Xiaomi", "Redmi")
        )

        val decision = engine.analyzeAndDecide(midProfile)
        assertTrue(decision.score in 50..95)
        assertTrue(decision.detectedBottlenecks.isNotEmpty())
        assertTrue(decision.detectedBottlenecks.any { it.title.contains("Vulkan") })
    }
}
