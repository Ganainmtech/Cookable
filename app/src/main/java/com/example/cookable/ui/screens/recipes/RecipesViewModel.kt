package com.example.cookable.ui.screens.recipes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookable.data.local.db.AppDatabase
import com.example.cookable.data.repository.CookableRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecipeUi(
    val id: Long,
    val name: String,
    val thumbnailUrl: String?
)

data class RecipesUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val recipes: List<RecipeUi> = emptyList(),
    val error: String? = null
)

class RecipesViewModel(application: Application) : AndroidViewModel(application) {

    private val repo: CookableRepository

    init {
        val db = AppDatabase.getInstance(application)
        repo = CookableRepository(
            cupboardDao = db.cupboardDao(),
            shoppingDao = db.shoppingDao()
        )
    }

    private val _uiState = MutableStateFlow(RecipesUiState())
    val uiState: StateFlow<RecipesUiState> = _uiState.asStateFlow()

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
    }

    fun search() {
        val currentQuery = _uiState.value.query.trim()
        if (currentQuery.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = repo.searchRecipes(currentQuery)
            val mapped = result.map { dto ->
                RecipeUi(
                    id = dto.id.toLongOrNull() ?: -1L,
                    name = dto.name,
                    thumbnailUrl = dto.thumbnailUrl
                )
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    recipes = mapped,
                    error = if (mapped.isEmpty()) "No recipes found." else null
                )
            }
        }
    }
}
