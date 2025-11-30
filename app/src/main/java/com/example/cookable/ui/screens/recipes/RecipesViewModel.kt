package com.example.cookable.ui.screens.recipes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookable.data.local.db.AppDatabase
import com.example.cookable.data.local.entity.RecipeEntity
import com.example.cookable.data.repository.CookableRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    private val recipeDao = AppDatabase.getInstance(application).recipeDao()

    private val _uiState = MutableStateFlow(RecipesUiState())
    val uiState: StateFlow<RecipesUiState> = _uiState.asStateFlow()

    init {
        val db = AppDatabase.getInstance(application)
        repo = CookableRepository(
            cupboardDao = db.cupboardDao(),
            shoppingDao = db.shoppingDao()
        )

        viewModelScope.launch {
            // 1) Ensure we have 3 local recipes if table is empty
            preloadLocalRecipesIfNeeded()

            // 2) Now observe local recipes continuously
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
                        thumbnailUrl = null,
                        isCookable = isCookable,
                        ingredients = ingredients,
                        instructions = entity.instructions
                    )
                }

                _uiState.update { it.copy(recipes = mappedLocal) }
            }
        }
    }

    private suspend fun preloadLocalRecipesIfNeeded() {
        withContext(Dispatchers.IO) {
            val existing = recipeDao.getAll().first()
            if (existing.isEmpty()) {
                recipeDao.insertAll(
                    listOf(
                        RecipeEntity(
                            name = "Pasta",
                            instructions = "Boil pasta. Drain. Add sauce.",
                            ingredients = "pasta,tomato sauce"
                        ),
                        RecipeEntity(
                            name = "Omelette",
                            instructions = "Beat eggs. Pour into pan. Fold.",
                            ingredients = "eggs,butter,salt"
                        ),
                        RecipeEntity(
                            name = "Salad",
                            instructions = "Chop vegetables. Mix together.",
                            ingredients = "lettuce,tomato,cucumber"
                        )
                    )
                )
            }
        }
    }

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

            // Convert API → RecipeUi list (API currently has no ingredient fields)
            val apiMapped = result.map { dto ->

                val rawIngredients = emptyList<String>()   // API has no ingredients for now

                RecipeUi(
                    id = dto.id.toLongOrNull() ?: -1L,
                    name = dto.name,
                    thumbnailUrl = dto.thumbnailUrl,
                    isCookable = false, // cannot determine without ingredients
                    ingredients = rawIngredients,
                    instructions = dto.instructions
                )
            }

            // Merge local + API
            val merged = _uiState.value.recipes + apiMapped

            _uiState.update {
                it.copy(
                    isLoading = false,
                    recipes = merged,
                    error = if (merged.isEmpty()) "No recipes found." else null
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
