package com.example.cookable.ui.screens.recipes

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
    val state by vm.uiState.collectAsState()

    // Whenever this screen is shown, recompute cookable status from cupboard
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
        Text("Recipes 🍜")

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

        val recipesToShow = if (state.showCookableOnly) {
            state.recipes.filter { it.isCookable }
        } else {
            state.recipes
        }

        Spacer(Modifier.height(8.dp))

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
                        Text(recipe.name)
                        Button(onClick = { onOpenRecipe(recipe.id.toString()) }) {
                            Text("View")
                        }
                    }

                    Text(
                        text = if (recipe.isCookable)
                            "Cookable with your cupboard ✅"
                        else
                            "Missing ingredients ❌"
                    )
                }
            }
        }

        if (state.error != null) {
            Spacer(Modifier.height(8.dp))
            Text(state.error!!)
        }
    }
}

@Composable
private fun androidViewModel(): RecipesViewModel {
    val app = LocalContext.current.applicationContext as Application
    return viewModel(factory = ViewModelProvider.AndroidViewModelFactory.getInstance(app))
}
