package com.example.cookable.ui.screens.recipes

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
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
import coil.compose.AsyncImage
import com.example.cookable.ui.screens.cupboard.CupboardViewModel
import com.example.cookable.ui.screens.shopping.ShoppingListViewModel

@Composable
fun RecipeDetailScreen(
    contentPadding: PaddingValues,
    recipeId: String,
    navController: NavController,
    recipesVm: RecipesViewModel = recipesAndroidViewModel(),
    cupboardVm: CupboardViewModel = cupboardAndroidViewModel(),
    shoppingVm: ShoppingListViewModel = shoppingAndroidViewModel()
) {
    // Look up the selected recipe from the shared RecipesViewModel
    val recipe = recipesVm.getRecipeById(recipeId)

    // Watch cupboard items so we can check which ingredients are available
    val cupboardState by cupboardVm.uiState.collectAsState()

    // Normalised cupboard ingredient names (lowercased)
    val cupboardNames = cupboardState.items
        .map { it.name.trim().lowercase() }
        .toSet()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // Back button
        Button(onClick = { navController.popBackStack() }) {
            Text("← Back")
        }

        if (recipe == null) {
            Text(
                "Recipe not found.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
            return@Column
        }

        // Main title + basic info
        Text(
            "Recipe Detail 📖",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            "Recipe ID: ${recipe.id}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            "Name: ${recipe.name}",
            style = MaterialTheme.typography.titleMedium
        )

        // Show image from TheMealDB if the API returned a thumbnail URL
        if (!recipe.thumbnailUrl.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            AsyncImage(
                model = recipe.thumbnailUrl,
                contentDescription = "Recipe photo for ${recipe.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        // Check if every ingredient is covered by the cupboard
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
                "You are missing some ingredients ❌",
            color = if (isCookableWithCupboard)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "Ingredients:",
            style = MaterialTheme.typography.titleMedium
        )

        // List ingredients + show if each one is in cupboard or missing
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // Collect missing ingredients for bulk add to shopping
            val missingIngredients = mutableListOf<String>()

            recipe.ingredients.forEach { ingredient ->
                val inCupboard = cupboardNames.any { cupboardName ->
                    val ing = ingredient.trim().lowercase()
                    cupboardName.contains(ing) || ing.contains(cupboardName)
                }

                if (!inCupboard) missingIngredients += ingredient

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        ingredient,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (inCupboard)
                                "In cupboard ✅"
                            else
                                "Missing ❌",
                            color = if (inCupboard)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        // Single ingredient to add to shopping
                        if (!inCupboard) {
                            Button(onClick = {
                                // Add missing ingredient to shopping list with simple defaults
                                shoppingVm.add(
                                    name = ingredient,
                                    quantity = "1",
                                    unit = "x"
                                )
                            }) {
                                Text("Add to shopping")
                            }
                        }
                    }
                }
            }

            // bulk add all missing ingredients to shopping
            if (missingIngredients.isNotEmpty()) {
                Button(
                    onClick = {
                        missingIngredients.forEach { ing ->
                            shoppingVm.add(
                                name = ing,
                                quantity = "1",
                                unit = "x"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add ALL missing to shopping")
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            "Instructions:",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            recipe.instructions ?: "No instructions available.",
            style = MaterialTheme.typography.bodyMedium
        )
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

@Composable
private fun shoppingAndroidViewModel(): ShoppingListViewModel {
    val app = LocalContext.current.applicationContext as Application
    return viewModel(factory = ViewModelProvider.AndroidViewModelFactory.getInstance(app))
}
