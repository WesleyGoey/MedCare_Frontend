package com.wesley.medcare.ui.route

import android.app.Application
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.wesley.medcare.ui.view.History.HistoryView
import com.wesley.medcare.ui.view.LoginRegister.LoginView
import com.wesley.medcare.ui.view.LoginRegister.RegisterView
import com.wesley.medcare.ui.view.Medicine.*
import com.wesley.medcare.ui.view.Schedule.ReminderView
import com.wesley.medcare.ui.viewmodel.MedicineViewModel
import com.wesley.medcare.ui.viewmodel.UserViewModel

// --- ENUM APPVIEW DENGAN IKON TERPISAH (SELECTED & UNSELECTED) ---
enum class AppView(
    val title: String,
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null
) {
    LoginView("Login"),
    RegisterView("Register"),
    HomeView("Home", Icons.Filled.Home, Icons.Outlined.Home),
    MedicineView("Meds", Icons.Filled.MedicalServices, Icons.Outlined.MedicalServices),
    ReminderView("Remind", Icons.Filled.Alarm, Icons.Outlined.Alarm),
    HistoryView("History", Icons.Filled.History, Icons.Outlined.History),
    ProfileView("Profile", Icons.Filled.Person, Icons.Outlined.Person),
    AddMedicineView("Add Medication"),
    EditMedicineView("Edit Medication")
}

data class BottomNavItem(
    val view: AppView,
    val label: String
)

@Composable
fun AppRoute() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val userViewModel: UserViewModel = remember {
        UserViewModel(context.applicationContext as Application)
    }

    val medicineViewModel: MedicineViewModel = viewModel(
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

    val showBottomBar = currentRoute in bottomNavItems.map { it.view.name }

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
                MedicineView(navController = navController, viewModel = medicineViewModel)
            }
            composable(route = AppView.ReminderView.name) {
                ReminderView(navController = navController)
            }
            composable(route = AppView.HistoryView.name) {
                HistoryView(navController = navController)
            }
            composable(route = AppView.ProfileView.name) {
                ProfileView(navController = navController)
            }
            composable(route = AppView.AddMedicineView.name) {
                AddMedicineView(navController = navController, viewModel = medicineViewModel)
            }
            composable(
                route = "MedicineInfoView/{medicineId}",
                arguments = listOf(
                    navArgument("medicineId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val medicineId = backStackEntry.arguments?.getInt("medicineId") ?: 0
                MedicineInfoView(
                    navController = navController,
                    medicineId = medicineId
                )
            }
            // Di file AppRoute.kt atau yang sejenisnya
            composable(
                route = "EditMedicineView/{medicineId}",
                arguments = listOf(navArgument("medicineId") { type = NavType.IntType })
            ) { backStackEntry ->
                val medicineId = backStackEntry.arguments?.getInt("medicineId") ?: 0
                EditMedicineView(medicineId = medicineId, navController = navController)
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
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp // Dibuat flat agar menyatu dengan background
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
                        imageVector = if (isSelected) item.view.selectedIcon!! else item.view.unselectedIcon!!,
                        contentDescription = item.label,
                        modifier = Modifier.size(26.dp), // Ukuran ikon seragam
                        tint = if (isSelected) Color(0xFF457AF9) else Color(0xFF8A94A6) // Perubahan warna
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color(0xFF457AF9) else Color(0xFF8A94A6) // Perubahan warna teks
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent // Menghilangkan background kapsul/pill
                )
            )
        }
    }
}