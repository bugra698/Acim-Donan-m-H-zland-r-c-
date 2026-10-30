package com.example.data.model

enum class HardwareTier(val label: String, val description: String) {
    TIER_S_PLUS("Tier S+ (Ultra Amiral Gemisi)", "En üst düzey oyun ve 3D grafik işlem gücü."),
    TIER_A("Tier A (Amiral Gemisi / Üst Segment)", "Tüm modern 3D oyunlar için yüksek performans."),
    TIER_B("Tier B (Performans Orta Segment)", "Akıcı 60 FPS grafik ve dengeli enerji verimliliği."),
    TIER_C("Tier C (Dengeli Giriş Segment)", "Günlük görevler ve hafif oyunlar için optimize edilebilir."),
    TIER_D("Tier D (Temel / Eski Mimari)", "Maksimum RAM ve CPU hafifletme gerektiren donanım.")
}

data class CpuInfo(
    val modelName: String,
    val coreCount: Int,
    val architecture: String,
    val hardware: String,
    val board: String,
    val maxFreqGhz: Float,
    val loadPercentage: Int
)

data class GpuInfo(
    val renderer: String,
    val vendor: String,
    val version: String,
    val maxTextureSize: Int,
    val vulkanSupported: Boolean,
    val vulkanVersionString: String,
    val astcSupported: Boolean,
    val extensionsCount: Int
)

data class DisplayInfo(
    val resolution: String,
    val densityDpi: Int,
    val currentRefreshRate: Float,
    val maxSupportedRefreshRate: Float,
    val supportedRefreshRates: List<Float>,
    val isHdrSupported: Boolean
)

data class MemoryStorageInfo(
    val totalRamMb: Long,
    val availRamMb: Long,
    val usedRamMb: Long,
    val ramUsagePercent: Int,
    val isLowMemory: Boolean,
    val totalStorageGb: Long,
    val freeStorageGb: Long
)

data class ThermalBatteryInfo(
    val temperatureC: Float,
    val batteryLevel: Int,
    val batteryVoltageMv: Int,
    val thermalStatus: String,
    val thermalStatusCode: Int,
    val isThrottlingRisk: Boolean
)

data class OsVersionInfo(
    val androidRelease: String,
    val apiLevel: Int,
    val securityPatch: String,
    val kernelVersion: String,
    val deviceBrand: String,
    val deviceModel: String
)

data class FullHardwareProfile(
    val cpu: CpuInfo,
    val gpu: GpuInfo,
    val display: DisplayInfo,
    val memory: MemoryStorageInfo,
    val thermal: ThermalBatteryInfo,
    val os: OsVersionInfo
)

data class HardwareDecision(
    val tier: HardwareTier,
    val score: Int, // 0 - 100
    val verdictSummary: String,
    val architectureAnalysis: String,
    val detectedBottlenecks: List<BottleneckItem>,
    val recommendedStrategy: String,
    val gamingCapabilityScore: Int,
    val rayTracingStatus: String,
    val expectedMaxFpsTarget: Int
)

data class BottleneckItem(
    val title: String,
    val description: String,
    val severity: BottleneckSeverity
)

enum class BottleneckSeverity {
    LOW, MEDIUM, HIGH
}

enum class OptimizationProfile(
    val title: String,
    val subtitle: String,
    val targetFps: Int,
    val iconName: String
) {
    TURBO_GAMING(
        title = "Turbo Gaming (Maksimum Güç)",
        subtitle = "En yüksek ekran yenileme hızı, agresif RAM boşaltma ve render önceliği",
        targetFps = 120,
        iconName = "speed"
    ),
    ESPORTS_LOW_LATENCY(
        title = "E-Spor Düşük Gecikme",
        subtitle = "Dokunmatik gecikmesini sıfıra indirme ve kare tampon kuyruğu temizleme",
        targetFps = 90,
        iconName = "flash_on"
    ),
    BALANCED_SMOOTH(
        title = "Dengeli ve Akıcı 60 FPS",
        subtitle = "Isınmayı önleyen sabit kare hızı ve enerji tasarruflu GPU yönetimi",
        targetFps = 60,
        iconName = "tune"
    ),
    COOL_BATTERY(
        title = "Serin Çalışma & Pil Koruma",
        subtitle = "Termal frekans düşüşlerini engellemek için dinamik saat kısma",
        targetFps = 60,
        iconName = "ac_unit"
    )
}

data class InstalledGameApp(
    val packageName: String,
    val appName: String,
    val isGame: Boolean,
    val isOptimized: Boolean = false
)
