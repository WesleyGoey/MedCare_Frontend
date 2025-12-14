import androidx.navigation.NavDestination.Companion.hierarchy
import com.wesley.medcare.ui.view.Medicine.HomeView

package com.wesley.medcare.ui.route

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.wesley.medcare.ui.view.Medicine.MedicineView
import com.wesley.medcare.ui.viewmodel.UserViewModel

enum class AppView(
    val title: String,
    val icon: ImageVector? = null
) {
    HomeView("Home", Icons.Default.Home),
    MedicineView("Medicine", Icons.Default.MedicalServices),
}

data class BottomNavItem(
    val view: AppView,
    val label: String
)

@Composable
fun MyBottomNavigationBar(
    navController: NavHostController,
    currentDestination: NavDestination?,
    items: List<BottomNavItem>
) {
    if (items.any { it.view.name == currentDestination?.route }) {
        NavigationBar {
            items.forEach { item ->
                NavigationBarItem(
                    icon = { Icon(item.view.icon!!, contentDescription = item.label) },
                    label = { Text(item.label) },
                    selected = currentDestination?.hierarchy?.any { it.route == item.view.name } == true,
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Soal2AppRoute() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val viewModel: UserViewModel = viewModel()

    val bottomNavItems = listOf(
        BottomNavItem(AppView.HomeView, "Home"),
        BottomNavItem(AppView.MedicineView, "Medicine")
    )

    Scaffold(
        bottomBar = {
            MyBottomNavigationBar(
                navController = navController,
                currentDestination = currentDestination,
                items = bottomNavItems
            )
        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = AppView.HomeView.name
        ) {
            composable(route = AppView.HomeView.name) {
                HomeView()
            }
            composable(route = AppView.MedicineView.name) {
                MedicineView()
            }
        }
    }
}
