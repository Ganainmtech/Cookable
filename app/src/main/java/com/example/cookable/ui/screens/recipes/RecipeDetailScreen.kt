package com.example.cookable.ui.screens.recipes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.PaddingValues

@Composable
fun RecipeDetailScreen(
    contentPadding: PaddingValues,
    recipeId: Long,
    navController: NavController? = null, // optional parameter
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (navController != null) {
            Button(onClick = { navController.popBackStack() }) {
                Text("← Back")
            }
        }

        Text("Recipe Detail 📖")
        Text("Recipe ID: $recipeId")
        Text("This is a dummy description which will be hooked up to API or DB later.")
    }
}
