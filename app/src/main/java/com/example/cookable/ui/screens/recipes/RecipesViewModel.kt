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
    val ingredients: List<String>,
    val instructions: String?
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

    private val _uiState = MutableStateFlow(RecipesUiState())
    val uiState: StateFlow<RecipesUiState> = _uiState.asStateFlow()

    init {
        val db = AppDatabase.getInstance(application)
        repo = CookableRepository(
            cupboardDao = db.cupboardDao(),
            shoppingDao = db.shoppingDao()
        )
        // No local recipes now – list starts empty and is filled by API searches.
    }

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
    }

    fun onShowCookableOnlyChange(checked: Boolean) {
        _uiState.update { it.copy(showCookableOnly = checked) }
    }

    /**
     * Recalculate "cookable" for all recipes based on the current cupboard contents.
     * Called when the Recipes screen is shown so the list stays in sync with changes
     * made from the detail screen.
     */
    fun refreshCookable() {
        viewModelScope.launch {
            val cupboardNames = repo.cupboardItems
                .first()
                .map { it.name.trim().lowercase() }

            _uiState.update { state ->
                val updated = state.recipes.map { recipe ->
                    val ingredientsLower = recipe.ingredients
                        .map { it.trim().lowercase() }

                    val isCookable =
                        ingredientsLower.isNotEmpty() &&
                                ingredientsLower.all { ing ->
                                    cupboardNames.any { c ->
                                        c.contains(ing) || ing.contains(c)
                                    }
                                }

                    recipe.copy(isCookable = isCookable)
                }

                state.copy(recipes = updated)
            }
        }
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

            // Convert API → RecipeUi list using ingredients from API DTO
            val apiMapped = result.map { dto ->
                val rawIngredients = dto.ingredients()
                val ingredientsLower = rawIngredients.map { it.lowercase() }

                val isCookable =
                    ingredientsLower.isNotEmpty() &&
                            ingredientsLower.all { ing ->
                                cupboardNames.any { c -> c.contains(ing) || ing.contains(c) }
                            }

                RecipeUi(
                    id = dto.id.toLongOrNull() ?: -1L,
                    name = dto.name,
                    thumbnailUrl = dto.thumbnailUrl,
                    isCookable = isCookable,
                    ingredients = rawIngredients,
                    instructions = dto.instructions
                )
            }

            // recipes list is API only for the current search
            _uiState.update {
                it.copy(
                    isLoading = false,
                    recipes = apiMapped,
                    error = if (apiMapped.isEmpty())
                        "No recipes found for \"$currentQuery\"."
                    else
                        null
                )
            }
        }
    }

    /**
     * Helper to find a recipe by its ID from the current search results.
     */
    fun getRecipeById(id: String): RecipeUi? {
        val longId = id.toLongOrNull() ?: return null
        return _uiState.value.recipes.firstOrNull { it.id == longId }
    }
}
