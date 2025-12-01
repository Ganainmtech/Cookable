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
 * UI model for a recipe
 */
data class RecipeUi(
    val id: Long,
    val name: String,
    val thumbnailUrl: String?,
    val isCookable: Boolean,
    val ingredients: List<String>,
    val instructions: String?
)

// State for the Recipes screen
data class RecipesUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val recipes: List<RecipeUi> = emptyList(),
    val showCookableOnly: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel for the Recipes screen
 * Combines local Room recipes and API recipes
 */
class RecipesViewModel(application: Application) : AndroidViewModel(application) {

    private val repo: CookableRepository
    private val recipeDao = AppDatabase.getInstance(application).recipeDao()

    private val _uiState = MutableStateFlow(RecipesUiState())
    val uiState: StateFlow<RecipesUiState> = _uiState.asStateFlow()

    init {
        val db = AppDatabase.getInstance(application)
        repo = CookableRepository(
            cupboardDao = db.cupboardDao(),
            shoppingDao = db.shoppingDao()
        )

        // Load local recipes immediately and keep them in state
        viewModelScope.launch {
            val cupboardNames = repo.cupboardItems.first().map { it.name.trim().lowercase() }

            recipeDao.getAll().collect { localRecipes ->
                val mappedLocal = localRecipes.map { entity ->
                    val ingredients = entity.ingredientsList()
                    val ingredientsLower = ingredients.map { it.lowercase() }

                    val isCookable =
                        ingredientsLower.isNotEmpty() &&
                                ingredientsLower.all { ing ->
                                    cupboardNames.any { c -> c.contains(ing) || ing.contains(c) }
                                }

                    RecipeUi(
                        id = entity.id,
                        name = entity.name,
                        thumbnailUrl = null, // local recipes have no thumbnail
                        isCookable = isCookable,
                        ingredients = ingredients,
                        instructions = entity.instructions
                    )
                }

                _uiState.update { it.copy(recipes = mappedLocal) }
            }
        }
    }

    // Update search query text
    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
    }

    // Toggle "show cookable only"
    fun onShowCookableOnlyChange(checked: Boolean) {
        _uiState.update { it.copy(showCookableOnly = checked) }
    }

    /**
     * Recalculate "cookable" for all recipes based on the current cupboard contents
     * Called when the Recipes screen is shown so the list stays in sync with changes
     * made from the detail screen
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

    // Call api and update recipe list
    fun search() {
        val currentQuery = _uiState.value.query.trim()
        if (currentQuery.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Load cupboard items and normalise
            val cupboardNames: List<String> = repo.cupboardItems
                .first()
                .map { it.name.trim().lowercase() }

            // Fetch api results
            val result = repo.searchRecipes(currentQuery)

            // Convert api to RecipeUi list using real ingredients from api
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

            // Keep local recipes and replace API recipes each search
            val localOnly = _uiState.value.recipes.filter { it.thumbnailUrl == null }
            val merged = localOnly + apiMapped

            _uiState.update {
                it.copy(
                    isLoading = false,
                    recipes = merged,
                    error = if (apiMapped.isEmpty()) "No recipes found for \"$currentQuery\"." else null
                )
            }
        }
    }

    /**
     * Helper to find a recipe by its ID from the current state
     */
    fun getRecipeById(id: String): RecipeUi? {
        val longId = id.toLongOrNull() ?: return null
        return _uiState.value.recipes.firstOrNull { it.id == longId }
    }
}
