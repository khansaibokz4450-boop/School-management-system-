package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AttendanceScreen
import com.example.ui.screens.BroadcastingScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DataExportScreen
import com.example.ui.screens.DeveloperRoyaltyScreen
import com.example.ui.screens.ExamManagementScreen
import com.example.ui.screens.FeeManagementScreen
import com.example.ui.screens.HomeworkDiaryScreen
import com.example.ui.screens.KioskAttendanceScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StaffPayrollScreen
import com.example.ui.screens.StudentIdCardScreen
import com.example.ui.screens.TimetableScreen
import com.example.ui.screens.TransportScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PrimaryGreen
import com.example.ui.viewmodel.SchoolViewModel

data class NavTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

class MainActivity : ComponentActivity() {
    private val viewModel: SchoolViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: SchoolViewModel) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    var activeSubScreen by rememberSaveable { mutableStateOf<String?>(null) }

    // 4 Primary BottomNavigation Destinations: Dashboard, Attendance, Fees, Settings
    val navTabs = listOf(
        NavTab(
            title = "Dashboard",
            selectedIcon = Icons.Filled.Dashboard,
            unselectedIcon = Icons.Outlined.Dashboard
        ),
        NavTab(
            title = "Attendance",
            selectedIcon = Icons.Filled.Fingerprint,
            unselectedIcon = Icons.Outlined.Fingerprint
        ),
        NavTab(
            title = "Fees",
            selectedIcon = Icons.Filled.Payments,
            unselectedIcon = Icons.Outlined.Payments
        ),
        NavTab(
            title = "Settings",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings
        )
    )

    // Back handling for sub-screens and secondary tabs
    if (activeSubScreen != null) {
        BackHandler {
            activeSubScreen = null
        }
    } else if (selectedTabIndex != 0) {
        BackHandler {
            selectedTabIndex = 0
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.testTag("main_bottom_navigation")
            ) {
                navTabs.forEachIndexed { index, tab ->
                    val isSelected = activeSubScreen == null && selectedTabIndex == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            activeSubScreen = null
                            selectedTabIndex = index
                        },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryGreen,
                            selectedTextColor = PrimaryGreen,
                            indicatorColor = PrimaryGreen.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.title.lowercase()}")
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
            when (activeSubScreen) {
                "STAFF" -> StaffPayrollScreen(viewModel = viewModel, onBack = { activeSubScreen = null })
                "BROADCAST" -> BroadcastingScreen(viewModel = viewModel, onBack = { activeSubScreen = null })
                "EXAMS" -> ExamManagementScreen(viewModel = viewModel, onBack = { activeSubScreen = null })
                "HOMEWORK" -> HomeworkDiaryScreen(viewModel = viewModel, onBack = { activeSubScreen = null })
                "ID_CARD" -> StudentIdCardScreen(viewModel = viewModel, onBack = { activeSubScreen = null })
                "TIMETABLE" -> TimetableScreen(viewModel = viewModel, onBack = { activeSubScreen = null })
                "TRANSPORT" -> TransportScreen(onBack = { activeSubScreen = null })
                "EXPORT" -> DataExportScreen(viewModel = viewModel, onBack = { activeSubScreen = null })
                "SAAS_LICENSE" -> DeveloperRoyaltyScreen(viewModel = viewModel, onBack = { activeSubScreen = null })
                "KIOSK" -> KioskAttendanceScreen(viewModel = viewModel, onBack = { activeSubScreen = null })
                else -> {
                    when (selectedTabIndex) {
                        0 -> DashboardScreen(
                            viewModel = viewModel,
                            onNavigateToTab = { destination ->
                                when (destination) {
                                    1 -> selectedTabIndex = 1 // Attendance
                                    2 -> selectedTabIndex = 2 // Fees
                                    3 -> activeSubScreen = "STAFF"
                                    4 -> activeSubScreen = "BROADCAST"
                                    5 -> activeSubScreen = "EXAMS"
                                    6 -> activeSubScreen = "HOMEWORK"
                                    7 -> activeSubScreen = "ID_CARD"
                                    8 -> activeSubScreen = "TIMETABLE"
                                    9 -> activeSubScreen = "TRANSPORT"
                                    10 -> activeSubScreen = "EXPORT"
                                    11 -> activeSubScreen = "SAAS_LICENSE"
                                    12 -> activeSubScreen = "KIOSK"
                                    else -> selectedTabIndex = 0
                                }
                            }
                        )
                        1 -> AttendanceScreen(
                            viewModel = viewModel,
                            onOpenKiosk = { activeSubScreen = "KIOSK" },
                            onBack = { selectedTabIndex = 0 }
                        )
                        2 -> FeeManagementScreen(
                            viewModel = viewModel,
                            onBack = { selectedTabIndex = 0 }
                        )
                        3 -> SettingsScreen(
                            onNavigateToStaff = { activeSubScreen = "STAFF" },
                            onNavigateToBroadcast = { activeSubScreen = "BROADCAST" },
                            onNavigateToRoyalty = { activeSubScreen = "SAAS_LICENSE" },
                            onBack = { selectedTabIndex = 0 }
                        )
                    }
                }
            }
        }
    }
}
