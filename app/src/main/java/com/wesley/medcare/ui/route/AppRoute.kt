package com.wesley.medcare.ui.route

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.wesley.medcare.ui.view.LoginRegister.LoginView
import com.wesley.medcare.ui.view.LoginRegister.RegisterView
import com.wesley.medcare.ui.view.Medicine.AddMedicineView
import com.wesley.medcare.ui.view.Medicine.HomeView
import com.wesley.medcare.ui.view.Medicine.MedicineView
import com.wesley.medcare.ui.view.Medicine.ProfileView

enum class AppView(
    val title: String,
    val icon: ImageVector? = null
) {
    LoginView("Login"),
    RegisterView("Register"),
    HomeView("Home", Icons.Filled.Home),
    MedicineView("Medicine", Icons.Filled.MedicalServices),
    ProfileView("Profile", Icons.Filled.Person),
    AddMedicineView("Add Medication")
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

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route
    val currentView = AppView.entries.find { it.name == currentRoute }

    val bottomNavItems = listOf(
        BottomNavItem(AppView.HomeView, "Home"),
        BottomNavItem(AppView.MedicineView, "Medicine"),
        BottomNavItem(AppView.ProfileView, "Profile")
    )

    // Show TopBar for all screens except Login/Register
    val showTopBar = currentRoute !in listOf(
        AppView.LoginView.name,
        AppView.RegisterView.name
    )

    // Show BottomBar only for main navigation items
    val showBottomBar = currentRoute in bottomNavItems.map { it.view.name }

    Scaffold(
        topBar = {
            if (showTopBar) {
                MyTopAppBar(
                    currentView = currentView,
                    canNavigateBack = navController.previousBackStackEntry != null
                            && currentRoute !in bottomNavItems.map { it.view.name },
                    navigateUp = { navController.navigateUp() }
                )
            }
        },
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
                    onSignIn = { email, password ->
                        // TODO: Handle login logic
                        navController.navigate(AppView.HomeView.name) {
                            popUpTo(AppView.LoginView.name) { inclusive = true }
                        }
                    },
                    onSignUpClick = {
                        navController.navigate(AppView.RegisterView.name)
                    }
                )
            }
            composable(route = AppView.RegisterView.name) {
                RegisterView(
                    onSignUp = { name, email, password ->
                        // TODO: Handle register logic
                        navController.navigate(AppView.LoginView.name) {
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
                MedicineView(navController = navController)
            }
            composable(route = AppView.ProfileView.name) {
                ProfileView(navController = navController)
            }
            composable(route = AppView.AddMedicineView.name) {
                AddMedicineView(onBack = { navController.popBackStack() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    currentView: AppView?,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = currentView?.title ?: "MedCare",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF2F93FF),
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White
        )
    )
}

@Composable
fun MyBottomNavigationBar(
    navController: NavHostController,
    currentDestination: NavDestination?,
    items: List<BottomNavItem>
) {
    NavigationBar(
        containerColor = Color.White,
        contentColor = Color(0xFF2F93FF)
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
