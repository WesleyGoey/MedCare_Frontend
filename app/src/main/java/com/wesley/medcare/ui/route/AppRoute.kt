package com.wesley.medcare.ui.route

import HomeView
import android.app.Application
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.wesley.medcare.ui.view.LoginRegister.LoginView
import com.wesley.medcare.ui.view.LoginRegister.RegisterView
import com.wesley.medcare.ui.view.Medicine.MedicineView
import com.wesley.medcare.ui.view.Medicine.ProfileView
import com.wesley.medcare.ui.view.Schedule.ReminderView
import com.wesley.medcare.ui.view.History.HistoryView
import com.wesley.medcare.ui.view.Medicine.AddMedicineView
import com.wesley.medcare.ui.view.Medicine.EditMedicineView
import com.wesley.medcare.ui.view.Medicine.EditProfileView
import com.wesley.medcare.ui.view.Medicine.MedicineInfoView
import com.wesley.medcare.ui.view.Schedule.AddReminderView
import com.wesley.medcare.ui.view.Schedule.EditReminderView
import com.wesley.medcare.ui.viewmodel.MedicineViewModel
import com.wesley.medcare.ui.viewmodel.UserViewModel
import com.wesley.medcare.ui.viewmodel.ScheduleViewModel // Pastikan ini diimport

enum class AppView(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    LoginView("Login"),
    RegisterView("Register"),
    HomeView("Home", Icons.Filled.Home),
    MedicineView("Meds", Icons.Filled.MedicalServices),
    AddMedicineView("Add Medication"),
    MedicineInfoView("Medication Info"),
    EditMedicineView("Edit Medication"),
    ReminderView("Remind", Icons.Filled.Notifications),
    AddReminderView("Add Reminder"),
    EditReminderView("Edit Reminder"),
    HistoryView("History", Icons.Filled.History),
    ProfileView("Profile", Icons.Filled.Person),
    EditProfileView("Edit Profile")
}

data class BottomNavItem(
    val view: AppView,
    val label: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AppRoute() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Initialize UserViewModel
    val userViewModel: UserViewModel = remember {
        UserViewModel(context.applicationContext as Application)
    }

    // Initialize MedicineViewModel
    val medicineViewModel: MedicineViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
            context.applicationContext as Application
        )
    )

    // TAMBAHKAN INI: Inisialisasi ScheduleViewModel agar bisa digunakan di MedicineView & Info
    val scheduleViewModel: ScheduleViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
            context.applicationContext as Application
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    val bottomNavItems = listOf(
        BottomNavItem(AppView.HomeView, "Home"),
        BottomNavItem(AppView.MedicineView, "Meds"),
        BottomNavItem(AppView.ReminderView, "Remind"),
        BottomNavItem(AppView.HistoryView, "History"),
        BottomNavItem(AppView.ProfileView, "Profile")
    )

    val bottomRoutes = bottomNavItems.map { it.view.name }
    val showBottomBar = currentRoute in bottomRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                MyBottomNavigationBar(
                    navController = navController,
                    currentDestination = currentDestination,
                    items = bottomNavItems
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = AppView.LoginView.name
        ) {
            composable(route = AppView.LoginView.name) {
                LoginView(
                    viewModel = userViewModel,
                    onNavigateToHome = {
                        navController.navigate(AppView.HomeView.name) {
                            popUpTo(AppView.LoginView.name) { inclusive = true }
                        }
                    },
                    onSignUpClick = { navController.navigate(AppView.RegisterView.name) }
                )
            }
            composable(route = AppView.RegisterView.name) {
                RegisterView(
                    viewModel = userViewModel,
                    onNavigateToHome = {
                        navController.navigate(AppView.HomeView.name) {
                            popUpTo(AppView.RegisterView.name) { inclusive = true }
                        }
                    },
                    onSignInClick = {
                        navController.popBackStack()
                    }
                )
            }
            composable(route = AppView.HomeView.name) {
                HomeView()
            }
            composable(route = AppView.MedicineView.name) {
                // UPDATE: Kirim medicineViewModel dan scheduleViewModel
                MedicineView(
                    navController = navController,
                    medicineViewModel = medicineViewModel,
                    scheduleViewModel = scheduleViewModel
                )
            }
            composable(route = AppView.ReminderView.name) {
                // UPDATE: Gunakan instance scheduleViewModel yang sama
                ReminderView(navController = navController, viewModel = scheduleViewModel)
            }

            composable(route = AppView.AddReminderView.name) {
                AddReminderView(onBack = { navController.popBackStack() })
            }
            composable(route = AppView.HistoryView.name) {
                HistoryView(navController = navController)
            }
            composable(route = AppView.ProfileView.name) {
                ProfileView(navController = navController)
            }
            composable(route = AppView.EditProfileView.name) {
                EditProfileView(navController = navController)
            }
            composable(route = AppView.AddMedicineView.name) {
                AddMedicineView(navController = navController, viewModel = medicineViewModel)
            }
            composable(
                route = "${AppView.MedicineInfoView.name}/{medicineId}",
                arguments = listOf(navArgument("medicineId") { type = NavType.IntType })
            ) { backStackEntry ->
                val medicineId = backStackEntry.arguments?.getInt("medicineId") ?: 0
                // UPDATE: Berikan ViewModel pendukung
                MedicineInfoView(
                    navController = navController,
                    medicineId = medicineId,
                    medicineViewModel = medicineViewModel,
                    scheduleViewModel = scheduleViewModel
                )
            }

            composable(
                route = "${AppView.EditMedicineView.name}/{medicineId}",
                arguments = listOf(navArgument("medicineId") { type = NavType.IntType })
            ) { backStackEntry ->
                val medicineId = backStackEntry.arguments?.getInt("medicineId") ?: 0
                EditMedicineView(medicineId = medicineId, navController = navController)
            }

            composable(
                route = "${AppView.EditReminderView.name}/{scheduleId}",
                arguments = listOf(navArgument("scheduleId") { type = NavType.IntType })
            ) { backStackEntry ->
                val scheduleId = backStackEntry.arguments?.getInt("scheduleId") ?: 0
                EditReminderView(navController = navController, scheduleId = scheduleId)
            }
        }
    }
}

@Composable
fun MyBottomNavigationBar(
    navController: NavHostController,
    currentDestination: NavDestination?,
    items: List<BottomNavItem>
) {
    val activeColor = Color(0xFF457AF9)
    val inactiveColor = Color(0xFF8A94A6)

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.height(80.dp)
    ) {
        items.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any {
                it.route == item.view.name
            } == true

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.view.name) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.view.icon!!,
                        contentDescription = item.label,
                        modifier = Modifier.size(26.dp),
                        tint = if (isSelected) activeColor else inactiveColor
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) activeColor else inactiveColor
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedIconColor = activeColor,
                    unselectedIconColor = inactiveColor,
                    selectedTextColor = activeColor,
                    unselectedTextColor = inactiveColor
                )
            )
        }
    }
}