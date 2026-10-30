package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HardwareInfoProvider
import com.example.data.model.FullHardwareProfile
import com.example.data.model.HardwareDecision
import com.example.domain.HardwareDecisionEngine
import com.example.domain.OptimizationEngine
import com.example.ui.screens.BenchmarkScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DecisionReportScreen
import com.example.ui.screens.GameLauncherScreen
import com.example.ui.screens.GpuTuningScreen
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberDark
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

enum class AppScreen(val title: String, val icon: ImageVector) {
    DASHBOARD("Hızlandırıcı", Icons.Default.Bolt),
    DECISION("Karar Raporu", Icons.Default.Psychology),
    TUNING("Grafik Ayarı", Icons.Default.Tune),
    BENCHMARK("FPS Testi", Icons.Default.Speed),
    GAMES("Oyunlar", Icons.Default.SportsEsports)
}

class MainActivity : ComponentActivity() {

    private lateinit var hardwareInfoProvider: HardwareInfoProvider
    private lateinit var hardwareDecisionEngine: HardwareDecisionEngine
    private lateinit var optimizationEngine: OptimizationEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        hardwareInfoProvider = HardwareInfoProvider(this)
        hardwareDecisionEngine = HardwareDecisionEngine()
        optimizationEngine = OptimizationEngine(this)

        setContent {
            MyApplicationTheme {
                MainAppRoot(
                    hardwareInfoProvider = hardwareInfoProvider,
                    hardwareDecisionEngine = hardwareDecisionEngine,
                    optimizationEngine = optimizationEngine
                )
            }
        }
    }
}

@Composable
fun MainAppRoot(
    hardwareInfoProvider: HardwareInfoProvider,
    hardwareDecisionEngine: HardwareDecisionEngine,
    optimizationEngine: OptimizationEngine
) {
    var currentScreen by remember { mutableStateOf(AppScreen.DASHBOARD) }

    // Hardware Profile & Decision state
    var hardwareProfile by remember {
        mutableStateOf(hardwareInfoProvider.getFullHardwareProfile())
    }

    val hardwareDecision = remember(hardwareProfile) {
        hardwareDecisionEngine.analyzeAndDecide(hardwareProfile)
    }

    val optimizationState by optimizationEngine.state.collectAsState()

    // Periodic telemetry update (updates CPU load, RAM, temperature every 3 seconds)
    LaunchedEffect(Unit) {
        while (isActive) {
            delay(3000)
            hardwareProfile = hardwareInfoProvider.getFullHardwareProfile()
        }
    }

    // Handle system back navigation cleanly
    BackHandler(enabled = currentScreen != AppScreen.DASHBOARD) {
        currentScreen = AppScreen.DASHBOARD
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = CyberSurface,
                modifier = Modifier.border(width = 1.dp, color = CyberCardBorder)
            ) {
                AppScreen.values().forEach { screen ->
                    val isSelected = currentScreen == screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = screen },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontFamily = FontFamily.Default
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CyberDark,
                            selectedTextColor = NeonCyan,
                            indicatorColor = NeonCyan,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_${screen.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CyberDark)
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.DASHBOARD -> DashboardScreen(
                    profile = hardwareProfile,
                    decision = hardwareDecision,
                    optimizationState = optimizationState,
                    optimizationEngine = optimizationEngine,
                    onNavigateToDecision = { currentScreen = AppScreen.DECISION }
                )

                AppScreen.DECISION -> DecisionReportScreen(
                    profile = hardwareProfile,
                    decision = hardwareDecision,
                    optimizationEngine = optimizationEngine,
                    onOptimizationApplied = { currentScreen = AppScreen.DASHBOARD }
                )

                AppScreen.TUNING -> GpuTuningScreen(
                    profile = hardwareProfile
                )

                AppScreen.BENCHMARK -> BenchmarkScreen(
                    profile = hardwareProfile,
                    decision = hardwareDecision,
                    optimizationState = optimizationState
                )

                AppScreen.GAMES -> GameLauncherScreen(
                    profile = hardwareProfile,
                    decision = hardwareDecision,
                    optimizationEngine = optimizationEngine
                )
            }
        }
    }
}
