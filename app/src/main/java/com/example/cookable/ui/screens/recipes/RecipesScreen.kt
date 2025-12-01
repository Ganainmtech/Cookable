package com.example.cookable.ui.screens.recipes

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.PaddingValues

@Composable
fun RecipesScreen(
    contentPadding: PaddingValues,
    onOpenRecipe: (String) -> Unit,
    vm: RecipesViewModel = androidViewModel()
) {
    // Observe Recipe UI state from the ViewModel
    val state by vm.uiState.collectAsState()

    // When this screen is shown, refresh what is cookable
    LaunchedEffect(Unit) {
        vm.refreshCookable()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Screen title using Material typography + theme colour
        Text(
            "Recipes 🍜",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        // Search bar + button
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = state.query,
                onValueChange = { vm.onQueryChange(it) },
                label = { Text("Search recipes (e.g. chicken)") }
            )
            Button(
                modifier = Modifier.alignByBaseline(),
                onClick = { vm.search() }
            ) {
                Text("Search")
            }
        }

        // toggle to filter recipes by cookable only
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Show only cookable with my cupboard")
            Switch(
                checked = state.showCookableOnly,
                onCheckedChange = { vm.onShowCookableOnlyChange(it) }
            )
        }

        // decide which recipes to show based on toggle
        val recipesToShow = if (state.showCookableOnly) {
            state.recipes.filter { it.isCookable }
        } else {
            state.recipes
        }

        Spacer(Modifier.height(8.dp))

        // list of recipes (both local and from API)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(recipesToShow, key = { it.id }) { recipe ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Recipe name as a slightly larger text
                        Text(
                            recipe.name,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Button(onClick = { onOpenRecipe(recipe.id.toString()) }) {
                            Text("View")
                        }
                    }

                    // Status styled with theme colours
                    Text(
                        text = if (recipe.isCookable)
                            "Cookable with your cupboard ✅"
                        else
                            "Missing ingredients ❌",
                        color = if (recipe.isCookable)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Simple error label if something went wrong
        if (state.error != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                state.error!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * Helper to create an AndroidViewModel from Compose
 */
@Composable
private fun androidViewModel(): RecipesViewModel {
    val app = LocalContext.current.applicationContext as Application
    return viewModel(factory = ViewModelProvider.AndroidViewModelFactory.getInstance(app))
}
