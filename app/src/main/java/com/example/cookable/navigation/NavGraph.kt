package com.example.cookable.navigation

import android.app.Application
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cookable.ui.screens.cupboard.CupboardScreen
import com.example.cookable.ui.screens.recipes.RecipeDetailScreen
import com.example.cookable.ui.screens.recipes.RecipesScreen
import com.example.cookable.ui.screens.recipes.RecipesViewModel
import com.example.cookable.ui.screens.shopping.ShoppingListScreen

/**
 * All route names used in the bottom navigation + detail screens
 */
object Routes {
    const val CUPBOARD = "cupboard"
    const val RECIPES = "recipes"
    const val SHOPPING = "shopping"
    const val RECIPE_DETAIL = "recipeDetail/{recipeId}"
}

@Composable
fun CookableNavGraph(
    navController: NavHostController,
    contentPadding: PaddingValues
) {
    // Create a shared RecipesViewModel so search results stay in memory
    val app = LocalContext.current.applicationContext as Application
    val recipesVm: RecipesViewModel =
        viewModel(factory = ViewModelProvider.AndroidViewModelFactory.getInstance(app))

    NavHost(
        navController = navController,
        startDestination = Routes.CUPBOARD
    ) {
        // Cupboard tab
        composable(Routes.CUPBOARD) {
            CupboardScreen(
                contentPadding = contentPadding,
                onGoToRecipes = { navController.navigate(Routes.RECIPES) }
            )
        }

        // recipes search screen
        composable(Routes.RECIPES) {
            RecipesScreen(
                contentPadding = contentPadding,
                onOpenRecipe = { id ->
                    navController.navigate("recipeDetail/$id")
                },
                vm = recipesVm   // shared VM
            )
        }

        // shopping list tab
        composable(Routes.SHOPPING) {
            ShoppingListScreen(contentPadding = contentPadding)
        }

        // recipe detail screen
        composable(
            route = Routes.RECIPE_DETAIL,
            arguments = listOf(
                navArgument("recipeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("recipeId") ?: ""

            RecipeDetailScreen(
                contentPadding = contentPadding,
                recipeId = id,
                navController = navController,
                recipesVm = recipesVm   // shared VM passed in
            )
        }
    }
}
