package com.example.cookable.ui.screens.shopping

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookable.data.local.db.AppDatabase
import com.example.cookable.data.local.entity.ShoppingItemEntity
import com.example.cookable.data.repository.CookableRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// UI state for the shopping list screen
data class ShoppingUiState(
    val items: List<ShoppingItemEntity> = emptyList()
)

/**
 * ViewModel for the Shopping List screen
 * Talks to the Room database through CookableRepository
 */
class ShoppingListViewModel(application: Application) : AndroidViewModel(application) {
    private val repo: CookableRepository

    init {
        // Build db + repository
        val db = AppDatabase.getInstance(application)
        repo = CookableRepository(
            cupboardDao = db.cupboardDao(),
            shoppingDao = db.shoppingDao()
        )
    }

    // Expose shopping items as StateFlow for Compose
    val uiState: StateFlow<ShoppingUiState> =
        repo.shoppingItems
            .map { ShoppingUiState(items = it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ShoppingUiState()
            )

    // add an item to the shopping list
    fun add(name: String, quantity: String?, unit: String?) =
        viewModelScope.launch { repo.addShoppingItem(name, quantity, unit) }

    // remove an item by ID
    fun remove(id: Long) =
        viewModelScope.launch { repo.removeShoppingItemById(id) }

    // move an item from shopping list into cupboard, then delete it from shopping
    fun moveToCupboard(item: ShoppingItemEntity) =
        viewModelScope.launch {
            repo.moveShoppingItemToCupboard(item)
        }
}
