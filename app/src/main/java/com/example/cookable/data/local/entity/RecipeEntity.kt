package com.example.cookable.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Local recipe stored in Room (simple assignment-friendly version).
 * Ingredients saved as a single comma-separated string.
 */
@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val instructions: String?,
    val ingredients: String
) {
    /**
     * Convert "eggs,butter" into a List<String>
     */
    fun ingredientsList(): List<String> =
        ingredients.split(",").map { it.trim() }.filter { it.isNotEmpty() }
}
