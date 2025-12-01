package com.example.cookable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cookable.navigation.CookableNavGraph
import com.example.cookable.navigation.Routes
import com.example.cookable.ui.theme.CookableTheme

// Single activity enty point for the app
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CookableTheme {
                CookableApp()
            }
        }
    }
}

// Simple model for a bottom navigation tab
private data class TabItem(
    val route: String,
    val label: String
)

@Composable
private fun CookableApp() {
    // NavController drives navigation between screens
    val navController = rememberNavController()

    // three tabs: Cupboard, Recipes, Shopping
    val tabs = listOf(
        TabItem(route = Routes.CUPBOARD, label = "Cupboard"),
        TabItem(route = Routes.RECIPES,  label = "Recipes"),
        TabItem(route = Routes.SHOPPING, label = "Shopping")
    )

    // observe current destination so we know which tab is selected
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDest = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEach { item ->
                    val selected = currentDest.isInHierarchy(item.route)
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            // Navigate to the tab route
                            // launchSingleTop avoids multiple copies of same screen
                            navController.navigate(item.route) {
                                launchSingleTop = true
                            }
                        },
                        icon = {},
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { padding ->
        // Main navigation host (handles which screen is visible)
        CookableNavGraph(
            navController = navController,
            contentPadding = padding
        )
    }
}

// Helper to check if the current destination is under a given route
private fun NavDestination?.isInHierarchy(route: String): Boolean =
    this?.hierarchy?.any { it.route == route } == true
