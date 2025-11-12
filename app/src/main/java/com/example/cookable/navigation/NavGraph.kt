package com.example.cookable.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cookable.ui.screens.cupboard.CupboardScreen
import com.example.cookable.ui.screens.recipes.RecipeDetailScreen
import com.example.cookable.ui.screens.recipes.RecipesScreen
import com.example.cookable.ui.screens.shopping.ShoppingListScreen

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
    NavHost(
        navController = navController,
        startDestination = Routes.CUPBOARD
    ) {
        composable(Routes.CUPBOARD) {
            CupboardScreen(
                contentPadding = contentPadding,
                onGoToRecipes = { navController.navigate(Routes.RECIPES) }
            )
        }
        composable(Routes.RECIPES) {
            RecipesScreen(
                contentPadding = contentPadding,
                onOpenRecipe = { id -> navController.navigate("recipeDetail/$id") }
            )
        }
        composable(Routes.SHOPPING) {
            ShoppingListScreen(contentPadding = contentPadding)
        }
        composable(
            route = Routes.RECIPE_DETAIL,
            arguments = listOf(
                navArgument("recipeId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("recipeId") ?: -1L
            RecipeDetailScreen(
                contentPadding = contentPadding,
                recipeId = id
            )
        }
    }
}
