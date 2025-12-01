package com.example.cookable.data.repository

import com.example.cookable.data.local.dao.CupboardDao
import com.example.cookable.data.local.dao.ShoppingDao
import com.example.cookable.data.local.entity.CupboardItemEntity
import com.example.cookable.data.local.entity.ShoppingItemEntity
import com.example.cookable.data.remote.api.RecipeApi
import com.example.cookable.data.remote.dto.RecipeDto
import kotlinx.coroutines.flow.Flow

/**
 * Repository that sits between the UI and the DAOs/API
 * keeps all data operations in one place
 */
class CookableRepository(
    private val cupboardDao: CupboardDao,
    private val shoppingDao: ShoppingDao,
    private val recipeApi: RecipeApi = RecipeApi.instance
) {

    // Live flow of cupboard items from Room
    val cupboardItems: Flow<List<CupboardItemEntity>> = cupboardDao.getAll()

    // Add an item into the cupboard
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

    // Move an item from shopping to cupboard
    suspend fun moveShoppingItemToCupboard(item: ShoppingItemEntity) {
        cupboardDao.insert(
            CupboardItemEntity(
                name = item.name,
                quantity = item.quantity,
                unit = item.unit
            )
        )
        shoppingDao.deleteById(item.id)
    }

    suspend fun removeCupboardItemById(id: Long) {
        cupboardDao.deleteById(id)
    }

    // live flow of shopping list items
    val shoppingItems: Flow<List<ShoppingItemEntity>> = shoppingDao.getAll()

    // add to shopping list
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

    // calls TheMealDB api and returns recipes
    suspend fun searchRecipes(query: String): List<RecipeDto> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return emptyList()

        return try {
            val response = recipeApi.searchRecipes(trimmed)
            response.meals ?: emptyList()   // empty list
        } catch (_: Exception) {
            emptyList()                     // fail safely
        }
    }
}
