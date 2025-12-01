package com.example.cookable.ui.screens.cupboard

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.PaddingValues

@Composable
fun CupboardScreen(
    contentPadding: PaddingValues,
    onGoToRecipes: () -> Unit,
    vm: CupboardViewModel = androidViewModel()
) {
    // local text field state
    var name by remember { mutableStateOf("") }
    var qty by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }

    // observe cupboard items from the ViewModel
    val state by vm.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Cupboard 🧺")

        // Inputs for item name, quantity and unit
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = name,
                onValueChange = { name = it },
                label = { Text("Item") }
            )
            OutlinedTextField(
                modifier = Modifier.width(100.dp),
                value = qty,
                onValueChange = { qty = it },
                label = { Text("Qty") }
            )
            OutlinedTextField(
                modifier = Modifier.width(100.dp),
                value = unit,
                onValueChange = { unit = it },
                label = { Text("Unit") }
            )
        }

        // add button + shortcut to Recipes tab
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                vm.add(name, qty.ifBlank { null }, unit.ifBlank { null })
                name = ""; qty = ""; unit = ""
            }) {
                Text("Add")
            }

            Button(onClick = onGoToRecipes) {
                Text("Find Recipes I Can Make →")
            }
        }

        Spacer(Modifier.height(8.dp))

        // list of cupboard items
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.items, key = { it.id }) { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val right = listOfNotNull(item.quantity, item.unit).joinToString(" ")
                    Text("${item.name}${if (right.isNotBlank()) " — $right" else ""}")

                    Button(onClick = { vm.remove(item.id) }) {
                        Text("🍽️ Remove")
                    }
                }
            }
        }
    }
}

/**
 * Helper to get an AndroidViewModel from inside Compose
 */
@Composable
private fun androidViewModel(): CupboardViewModel {
    val app = LocalContext.current.applicationContext as Application
    return viewModel(factory = ViewModelProvider.AndroidViewModelFactory.getInstance(app))
}
