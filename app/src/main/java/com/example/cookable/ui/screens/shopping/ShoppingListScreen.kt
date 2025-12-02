package com.example.cookable.ui.screens.shopping

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
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

@Composable
fun ShoppingListScreen(
    contentPadding: PaddingValues,
    vm: ShoppingListViewModel = androidViewModel()
) {
    // Local text field state
    var name by remember { mutableStateOf("") }
    var qty by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }

    // Observe shopping list items from the ViewModel
    val state by vm.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Screen title styled with Material typography + theme colour
        Text(
            "Shopping List 🛍️",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

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

        // Button to add a new item into the shopping list
        Button(onClick = {
            vm.add(name, qty.ifBlank { null }, unit.ifBlank { null })
            name = ""; qty = ""; unit = ""
        }) {
            Text("Add to shopping list")
        }

        Spacer(Modifier.height(8.dp))

        // List of shopping items
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.items, key = { it.id }) { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val right = listOfNotNull(item.quantity, item.unit).joinToString(" ")

                    // Name styled with bodyLarge, single line, ellipsis, and weight
                    Text(
                        text = "${item.name}${if (right.isNotBlank()) " — $right" else ""}",
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // Actions: mark as bought (move to cupboard) or delete
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = { vm.moveToCupboard(item) }) {
                            Text("📦 Bought")
                        }
                        Button(onClick = { vm.remove(item.id) }) {
                            Text("🗑️")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Helper to get ShoppingListViewModel from inside Compose
 */
@Composable
private fun androidViewModel(): ShoppingListViewModel {
    val app = LocalContext.current.applicationContext as Application
    return viewModel(factory = ViewModelProvider.AndroidViewModelFactory.getInstance(app))
}
