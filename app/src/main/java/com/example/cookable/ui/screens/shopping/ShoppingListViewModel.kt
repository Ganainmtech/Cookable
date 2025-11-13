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

data class ShoppingUiState(
    val items: List<ShoppingItemEntity> = emptyList()
)

class ShoppingListViewModel(application: Application) : AndroidViewModel(application) {
    private val repo: CookableRepository

    init {
        val db = AppDatabase.getInstance(application)
        repo = CookableRepository(
            cupboardDao = db.cupboardDao(),
            shoppingDao = db.shoppingDao()
        )
    }

    val uiState: StateFlow<ShoppingUiState> =
        repo.shoppingItems
            .map { ShoppingUiState(items = it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ShoppingUiState())

    fun add(name: String, quantity: String?, unit: String?) =
        viewModelScope.launch { repo.addShoppingItem(name, quantity, unit) }

    fun remove(id: Long) =
        viewModelScope.launch { repo.removeShoppingItemById(id) }
}
