package com.example.cookable.data.repository

import com.example.cookable.data.local.dao.CupboardDao
import com.example.cookable.data.local.entity.CupboardItemEntity
import com.example.cookable.data.remote.api.RecipeApi
import com.example.cookable.data.remote.dto.RecipeDto
import kotlinx.coroutines.flow.Flow

class CookableRepository(
    private val cupboardDao: CupboardDao,
    private val recipeApi: RecipeApi = RecipeApi.instance // default API instance
) {
    // --- Room: Cupboard ---

    val cupboardItems: Flow<List<CupboardItemEntity>> = cupboardDao.getAll()

    suspend fun addCupboardItem(name: String, quantity: String?, unit: String?) {
        if (name.isBlank()) return
        cupboardDao.insert(
            CupboardItemEntity(
                name = name.trim(),
                quantity = quantity?.trim(),
                unit = unit?.trim()
            )
        )
    }

    suspend fun removeCupboardItemById(id: Long) {
        cupboardDao.deleteById(id)
    }

    // --- API: Recipes (TheMealDB) ---

    /**
     * Search recipes from the remote API.
     * Returns an empty list if query is blank or if any error occurs.
     */
    suspend fun searchRecipes(query: String): List<RecipeDto> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return emptyList()

        return try {
            val response = recipeApi.searchRecipes(trimmed)
            response.meals ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }
}
