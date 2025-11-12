package com.example.cookable.ui.screens.cupboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.PaddingValues

@Composable
fun CupboardScreen(
    contentPadding: PaddingValues,
    onGoToRecipes: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Cupboard 🧺")
        Text("This will show items from the Room database (dummy).")
        Button(onClick = onGoToRecipes) {
            Text("Find Recipes I Can Make →")
        }
    }
}
