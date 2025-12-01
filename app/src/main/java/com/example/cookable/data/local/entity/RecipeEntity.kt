package com.example.cookable.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Local recipe stored in Room.
 * Used for the 3 get started recipes included with the app
 */
@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,              // recipe title
    val instructions: String?,     // simple text instructions
    val ingredients: String        // e.g, "eggs,butter,salt"
) {
    /**
     * Convert the ingredients string into a usable List
     */
    fun ingredientsList(): List<String> =
        ingredients.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
}
