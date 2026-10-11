package com.example.kitabu_madb372_sf1.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kitabu_madb372_sf1.ui.screens.CatalogScreen
import com.example.kitabu_madb372_sf1.ui.screens.DashboardScreen
import com.example.kitabu_madb372_sf1.ui.viewmodel.CatalogViewModel
import com.example.kitabu_madb372_sf1.ui.viewmodel.DashboardViewModel
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.graphics.Color

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.ui.unit.dp
import com.example.kitabu_madb372_sf1.ui.theme.KitabuLavender
import com.example.kitabu_madb372_sf1.ui.theme.KitabuPurple
import com.example.kitabu_madb372_sf1.ui.theme.KitabuYellow

private const val CATALOG_ROUTE = "catalog"
private const val DASHBOARD_ROUTE = "dashboard"

@Composable
fun AppNavigation(
    catalogViewModel: CatalogViewModel,
    dashboardViewModel: DashboardViewModel
) {
    // Handles the navigation between screens.
    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFFF9F4FF),
                tonalElevation = 0.dp,
                modifier = Modifier.height(92.dp)
            ) {

                NavigationBarItem(
                    selected = currentRoute == CATALOG_ROUTE,
                    onClick = {
                        navController.navigate(CATALOG_ROUTE) {
                            popUpTo(
                                navController.graph
                                    .findStartDestination()
                                    .id
                            ) {
                                saveState = true
                            }

                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "Catalog"
                        )
                    },
                    label = {
                        Text("Catalog")
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = KitabuPurple,
                        selectedTextColor = KitabuPurple,
                        indicatorColor = KitabuYellow,
                        unselectedIconColor = Color(0xFF7E718F),
                        unselectedTextColor = Color(0xFF7E718F)
                    )
                )

                NavigationBarItem(
                    selected = currentRoute == DASHBOARD_ROUTE,
                    onClick = {
                        navController.navigate(DASHBOARD_ROUTE) {
                            popUpTo(
                                navController.graph
                                    .findStartDestination()
                                    .id
                            ) {
                                saveState = true
                            }

                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "My Reservations"
                        )
                    },
                    label = {
                        Text("My Reservations")
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = KitabuPurple,
                        selectedTextColor = KitabuPurple,
                        indicatorColor = KitabuYellow,
                        unselectedIconColor = Color(0xFF7E718F),
                        unselectedTextColor = Color(0xFF7E718F)
                    )
                )
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = CATALOG_ROUTE,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(CATALOG_ROUTE) {
                CatalogScreen(
                    viewModel = catalogViewModel
                )
            }

            composable(DASHBOARD_ROUTE) {
                DashboardScreen(
                    viewModel = dashboardViewModel
                )
            }
        }
    }
}