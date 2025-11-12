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

private data class TabItem(
    val route: String,
    val label: String
)

@Composable
private fun CookableApp() {
    val navController = rememberNavController()

    val tabs = listOf(
        TabItem(route = Routes.CUPBOARD, label = "Cupboard"),
        TabItem(route = Routes.RECIPES,  label = "Recipes"),
        TabItem(route = Routes.SHOPPING, label = "Shopping")
    )

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
                            // SIMPLE: always navigate to that route
                            navController.navigate(item.route) {
                                launchSingleTop = true   // avoid duplicates of same screen
                            }
                        },
                        icon = {}, // keep it plain for now
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { padding ->
        CookableNavGraph(
            navController = navController,
            contentPadding = padding
        )
    }
}

private fun NavDestination?.isInHierarchy(route: String): Boolean =
    this?.hierarchy?.any { it.route == route } == true
