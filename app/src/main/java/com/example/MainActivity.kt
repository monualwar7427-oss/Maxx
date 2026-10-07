package com.example

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AlarmActiveScreen
import com.example.ui.screens.ArmedScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.IntruderLogScreen
import com.example.ui.screens.MaxVoiceScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {

    private val viewModel: TouchGuardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Allow app to show on lock screen and wake up when alarm triggers
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            MyApplicationTheme {
                MainAppContainer(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContainer(viewModel: TouchGuardViewModel) {
    val status by viewModel.status.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

    // Back handling for custom navigation
    BackHandler(enabled = true) {
        when {
            status == GuardStatus.ALARM_ACTIVE -> {
                // Must enter PIN, cannot exit via back key
            }
            status == GuardStatus.ARMED -> {
                // Must enter PIN to disarm
            }
            status == GuardStatus.COUNTDOWN -> {
                viewModel.cancelArming()
            }
            selectedTab != 0 -> {
                viewModel.setSelectedTab(0)
            }
        }
    }

    // Full screen overlays based on Guard status
    when (status) {
        GuardStatus.ALARM_ACTIVE -> {
            AlarmActiveScreen(viewModel = viewModel)
        }
        GuardStatus.ARMED -> {
            ArmedScreen(viewModel = viewModel)
        }
        else -> {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = CyberBackground,
                bottomBar = {
                    NavigationBar(
                        containerColor = CyberSurface,
                        tonalElevation = 8.dp
                    ) {
                        val navItems = listOf(
                            Triple(0, "Guard", Icons.Default.Shield),
                            Triple(1, "Max Voice", Icons.Default.Mic),
                            Triple(2, "Incidents", Icons.Default.History),
                            Triple(3, "Settings", Icons.Default.Settings)
                        )

                        navItems.forEach { (tabIndex, label, icon) ->
                            val isSelected = selectedTab == tabIndex
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { viewModel.setSelectedTab(tabIndex) },
                                icon = {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        tint = if (isSelected) CyberCyan else TextSecondary
                                    )
                                },
                                label = {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        color = if (isSelected) CyberCyan else TextSecondary
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = CyberCyan.copy(alpha = 0.15f)
                                ),
                                modifier = Modifier.testTag("nav_tab_$tabIndex")
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (selectedTab) {
                        0 -> DashboardScreen(
                            viewModel = viewModel,
                            onNavigateToMax = { viewModel.setSelectedTab(1) }
                        )
                        1 -> MaxVoiceScreen(viewModel = viewModel)
                        2 -> IntruderLogScreen(viewModel = viewModel)
                        3 -> SettingsScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
