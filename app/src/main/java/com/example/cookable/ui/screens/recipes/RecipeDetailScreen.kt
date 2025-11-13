package com.example.cookable.ui.screens.recipes

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.foundation.layout.PaddingValues
import com.example.cookable.ui.screens.cupboard.CupboardViewModel

@Composable
fun RecipeDetailScreen(
    contentPadding: PaddingValues,
    recipeId: Long,
    navController: NavController,
    recipesVm: RecipesViewModel = recipesAndroidViewModel(),
    cupboardVm: CupboardViewModel = cupboardAndroidViewModel()
) {
    // Get the selected recipe from RecipesViewModel
    val recipe = recipesVm.getRecipeById(recipeId)

    // Observe cupboard state (same pattern as CupboardScreen)
    val cupboardState by cupboardVm.uiState.collectAsState()

    // Normalised cupboard ingredient names (lowercased)
    val cupboardNames = cupboardState.items
        .map { it.name.trim().lowercase() }
        .toSet()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // Back button
        Button(onClick = { navController.popBackStack() }) {
            Text("← Back")
        }

        if (recipe == null) {
            Text("Recipe not found.")
            return@Column
        }

        Text("Recipe Detail 📖")
        Text("Recipe ID: ${recipe.id}")
        Text("Name: ${recipe.name}")

        Spacer(Modifier.height(8.dp))

        val isCookableWithCupboard = recipe.ingredients
            .map { it.trim().lowercase() }
            .all { ing ->
                cupboardNames.any { cupboardName ->
                    cupboardName.contains(ing) || ing.contains(cupboardName)
                }
            }

        Text(
            text = if (isCookableWithCupboard)
                "Cookable with your cupboard ✅"
            else
                "You are missing some ingredients ❌"
        )

        Spacer(Modifier.height(8.dp))

        Text("Ingredients:")

        // Simple scroll-less list for ingredients (fine for assignment)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            recipe.ingredients.forEach { ingredient ->
                val inCupboard = cupboardNames.any { cupboardName ->
                    val ing = ingredient.trim().lowercase()
                    cupboardName.contains(ing) || ing.contains(cupboardName)
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(ingredient)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (inCupboard)
                                "In cupboard ✅"
                            else
                                "Missing ❌"
                        )

                        if (!inCupboard) {
                            Button(onClick = {
                                // Add missing ingredient to shopping list
                                cupboardVm.add(ingredient, null, null)
                            }) {
                                Text("Add to cupboard")
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Helpers to get AndroidViewModels inside this composable.
 */
@Composable
private fun recipesAndroidViewModel(): RecipesViewModel {
    val app = LocalContext.current.applicationContext as Application
    return viewModel(factory = ViewModelProvider.AndroidViewModelFactory.getInstance(app))
}

@Composable
private fun cupboardAndroidViewModel(): CupboardViewModel {
    val app = LocalContext.current.applicationContext as Application
    return viewModel(factory = ViewModelProvider.AndroidViewModelFactory.getInstance(app))
}
