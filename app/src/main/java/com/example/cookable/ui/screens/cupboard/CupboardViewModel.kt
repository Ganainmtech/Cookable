package com.example.cookable.ui.screens.cupboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookable.data.local.db.AppDatabase
import com.example.cookable.data.repository.CookableRepository
import com.example.cookable.data.local.entity.CupboardItemEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CupboardUiState(
    val items: List<CupboardItemEntity> = emptyList()
)

class CupboardViewModel(application: Application) : AndroidViewModel(application) {
    private val repo: CookableRepository

    init {
        val db = AppDatabase.getInstance(application)
        repo = CookableRepository(
            cupboardDao = db.cupboardDao(),
            shoppingDao = db.shoppingDao()
        )
    }

    val uiState: StateFlow<CupboardUiState> =
        repo.cupboardItems
            .map { CupboardUiState(items = it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CupboardUiState())

    fun add(name: String, quantity: String?, unit: String?) =
        viewModelScope.launch { repo.addCupboardItem(name, quantity, unit) }

    fun remove(id: Long) =
        viewModelScope.launch { repo.removeCupboardItemById(id) }
}
