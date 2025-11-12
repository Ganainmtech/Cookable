package com.example.cookable.ui.screens.recipes

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Recipes 🍝")

        // Search row
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

        // Loading indicator
        if (state.isLoading) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            }
        }

        // Error / empty message
        state.error?.let { err ->
            Text(text = err)
        }

        // Results list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.recipes, key = { it.id }) { recipe ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (recipe.id != -1L) {
                                onOpenRecipe(recipe.id)
                            }
                        }
                        .padding(8.dp)
                ) {
                    Text(recipe.name)
                    if (recipe.thumbnailUrl != null) {
                        Text(
                            text = "Image: ${recipe.thumbnailUrl}",
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Helper that builds an AndroidViewModel (needs Application).
 * Same pattern as we used in CupboardScreen.
 */
@Composable
private fun androidViewModel(): RecipesViewModel {
    val app = LocalContext.current.applicationContext as Application
    return viewModel(factory = ViewModelProvider.AndroidViewModelFactory.getInstance(app))
}
