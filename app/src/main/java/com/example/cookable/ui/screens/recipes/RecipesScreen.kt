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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Alignment
import coil.compose.AsyncImage

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

        // Toggle to filter recipes by cookable only
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Show Cookable Recipes")
            Switch(
                checked = state.showCookableOnly,
                onCheckedChange = { vm.onShowCookableOnlyChange(it) }
            )
        }

        // Decide which recipes to show based on toggle
        val recipesToShow = if (state.showCookableOnly) {
            state.recipes.filter { it.isCookable }
        } else {
            state.recipes
        }

        Spacer(Modifier.height(8.dp))

        // List of recipes (from API)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(recipesToShow, key = { it.id }) { recipe ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Small thumbnail from API if available
                    if (!recipe.thumbnailUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = recipe.thumbnailUrl,
                            contentDescription = recipe.name,
                            modifier = Modifier.size(72.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Recipe name as a slightly larger text, single line with ellipsis
                            Text(
                                text = recipe.name,
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
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
