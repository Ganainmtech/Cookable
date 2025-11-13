package com.example.cookable.ui.screens.recipes

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.PaddingValues

@Composable
fun RecipesScreen(
    contentPadding: PaddingValues,
    onOpenRecipe: (Long) -> Unit,
    vm: RecipesViewModel = androidViewModel()
) {
    val state by vm.uiState.collectAsState()

    // Decide which recipes to show based on toggle
    val displayedRecipes =
        if (state.showCookableOnly) state.recipes.filter { it.isCookable }
        else state.recipes

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text("Recipes 🍝")

        // SEARCH BAR
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = state.query,
                onValueChange = vm::onQueryChange,
                label = { Text("Search recipes (e.g. chicken)") }
            )

            Button(
                modifier = Modifier.alignByBaseline(),
                onClick = { vm.search() }
            ) {
                Text("Search")
            }
        }

        // TOGGLE: SHOW ONLY COOKABLE
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Show only cookable with my cupboard")
            Switch(
                checked = state.showCookableOnly,
                onCheckedChange = vm::onShowCookableOnlyChange
            )
        }

        // LOADING SPINNER
        if (state.isLoading) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            }
        }

        // ERROR MESSAGE
        state.error?.let { err ->
            Text(text = err)
        }

        // RECIPES LIST
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = displayedRecipes,
                key = { it.id }
            ) { recipe: RecipeUi ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (recipe.id != -1L) onOpenRecipe(recipe.id)
                        }
                        .padding(8.dp)
                ) {

                    Text(recipe.name)

                    // Cookable label
                    Text(
                        text = if (recipe.isCookable)
                            "Cookable with your cupboard ✅"
                        else
                            "Missing ingredients ❌",
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    // Thumbnail URL (basic text for now)
                    recipe.thumbnailUrl?.let {
                        Text(
                            text = "Image: $it",
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * AndroidViewModel helper (same pattern as Cupboard + Shopping).
 */
@Composable
private fun androidViewModel(): RecipesViewModel {
    val app = LocalContext.current.applicationContext as Application
    return viewModel(factory = ViewModelProvider.AndroidViewModelFactory.getInstance(app))
}
