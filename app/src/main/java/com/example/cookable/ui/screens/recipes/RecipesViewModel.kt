package com.example.cookable.ui.screens.recipes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookable.data.local.db.AppDatabase
import com.example.cookable.data.repository.CookableRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI-friendly recipe model including whether it's cookable.
 */
data class RecipeUi(
    val id: Long,
    val name: String,
    val thumbnailUrl: String?,
    val isCookable: Boolean,
    val ingredients: List<String>
)

data class RecipesUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val recipes: List<RecipeUi> = emptyList(),
    val showCookableOnly: Boolean = false,
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

    fun onShowCookableOnlyChange(checked: Boolean) {
        _uiState.update { it.copy(showCookableOnly = checked) }
    }

    fun search() {
        val currentQuery = _uiState.value.query.trim()
        if (currentQuery.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Load cupboard items and normalize
            val cupboardNames: List<String> = repo.cupboardItems
                .first()
                .map { it.name.trim().lowercase() }

            // Fetch API results
            val result = repo.searchRecipes(currentQuery)

            // Map API to UI and determine cookability
            val mapped = result.map { dto ->
                val ingredientsRaw = dto.ingredients()
                val ingredientsLower = ingredientsRaw
                    .map { it.trim().lowercase() }

                val isCookable =
                    ingredientsLower.isNotEmpty() &&
                            ingredientsLower.all { ing ->
                                cupboardNames.any { cupboardName ->
                                    cupboardName.contains(ing) || ing.contains(cupboardName)
                                }
                            }

                RecipeUi(
                    id = dto.id.toLongOrNull() ?: -1L,
                    name = dto.name,
                    thumbnailUrl = dto.thumbnailUrl,
                    isCookable = isCookable,
                    ingredients = ingredientsRaw // keep original-cased for display
                )
            }

            // Update state
            _uiState.update {
                it.copy(
                    isLoading = false,
                    recipes = mapped,
                    error = if (mapped.isEmpty()) "No recipes found." else null
                )
            }
        }
    }

    /**
     * Helper to find a recipe by its ID from the current search results.
     */
    fun getRecipeById(id: Long): RecipeUi? =
        _uiState.value.recipes.firstOrNull { it.id == id }
}
