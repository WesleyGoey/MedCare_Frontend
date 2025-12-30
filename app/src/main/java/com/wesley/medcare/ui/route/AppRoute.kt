// kotlin
package com.wesley.medcare.ui.route

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
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
import com.wesley.medcare.data.container.AppContainer
import com.wesley.medcare.ui.view.LoginRegister.LoginView
import com.wesley.medcare.ui.view.LoginRegister.RegisterView
import com.wesley.medcare.ui.view.Medicine.HomeView
import com.wesley.medcare.ui.view.Medicine.MedicineView
import com.wesley.medcare.ui.view.Medicine.ProfileView
import com.wesley.medcare.ui.view.Schedule.ReminderView
import com.wesley.medcare.ui.view.History.HistoryView
import com.wesley.medcare.ui.view.Medicine.AddMedicineView
import com.wesley.medcare.ui.view.Medicine.EditMedicineView
import com.wesley.medcare.ui.view.Medicine.MedicineInfoView
import com.wesley.medcare.ui.viewmodel.MedicineViewModel
import com.wesley.medcare.ui.viewmodel.UserViewModel
import kotlinx.coroutines.launch

enum class AppView(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    LoginView("Login"),
    RegisterView("Register"),
    HomeView("Home", Icons.Filled.Home),
    MedicineView("Meds", Icons.Filled.MedicalServices),
    ReminderView("Remind", Icons.Filled.Notifications),
    HistoryView("History", Icons.Filled.History),
    ProfileView("Profile", Icons.Filled.Person),
    AddMedicineView("Add Medication"),
    EditMedicineView("Edit Medication")
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

    // Initialize MedicineViewModel using AndroidViewModelFactory so it's lifecycle-aware
    val medicineViewModel: MedicineViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
            context.applicationContext as Application
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route
    val currentView = AppView.entries.find { it.name == currentRoute }

    val bottomNavItems = listOf(
        BottomNavItem(AppView.HomeView, "Home"),
        BottomNavItem(AppView.MedicineView, "Meds"),
        BottomNavItem(AppView.ReminderView, "Remind"),
        BottomNavItem(AppView.HistoryView, "History"),
        BottomNavItem(AppView.ProfileView, "Profile")
    )

    val showTopBar = currentRoute !in listOf(
        AppView.LoginView.name,
        AppView.RegisterView.name
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
        contentColor = Color(0xFF457AF9)
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.view.icon!!,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = currentDestination?.hierarchy?.any {
                    it.route == item.view.name
                } == true,
                onClick = {
                    navController.navigate(item.view.name) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
