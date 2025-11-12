package com.example.cookable.ui.screens.recipes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.PaddingValues

@Composable
fun RecipesScreen(
    contentPadding: PaddingValues,
    onOpenRecipe: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Recipes 🍝")
        Text("This will list recipes fetched from the API (dummy).")
        Button(onClick = { onOpenRecipe(1L) }) {
            Text("Open Recipe Detail Example")
        }
    }
}
