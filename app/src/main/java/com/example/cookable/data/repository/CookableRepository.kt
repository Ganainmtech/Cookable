package com.example.cookable.data.repository

import com.example.cookable.data.local.dao.CupboardDao
import com.example.cookable.data.local.dao.ShoppingDao
import com.example.cookable.data.local.entity.CupboardItemEntity
import com.example.cookable.data.local.entity.ShoppingItemEntity
import com.example.cookable.data.remote.api.RecipeApi
import com.example.cookable.data.local.dao.RecipeDto
import kotlinx.coroutines.flow.Flow

class CookableRepository(
    private val cupboardDao: CupboardDao,
    private val shoppingDao: ShoppingDao,
    private val recipeApi: RecipeApi = RecipeApi.instance
) {

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

    val shoppingItems: Flow<List<ShoppingItemEntity>> = shoppingDao.getAll()

    suspend fun addShoppingItem(name: String, quantity: String?, unit: String?) {
        if (name.isBlank()) return
        shoppingDao.insert(
            ShoppingItemEntity(
                name = name.trim(),
                quantity = quantity?.trim(),
                unit = unit?.trim()
            )
        )
    }

    suspend fun removeShoppingItemById(id: Long) {
        shoppingDao.deleteById(id)
    }

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
