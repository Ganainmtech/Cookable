package com.example.cookable.data.local.dao

import com.google.gson.annotations.SerializedName

/**
 * Response wrapper from TheMealDB search endpoint
 */
data class RecipeSearchResponse(
    @SerializedName("meals") val meals: List<RecipeDto>?
)

/**
 * Minimal recipe info + a few ingredient fields
 */
data class RecipeDto(
    @SerializedName("idMeal") val id: String,
    @SerializedName("strMeal") val name: String,
    @SerializedName("strInstructions") val instructions: String?,
    @SerializedName("strMealThumb") val thumbnailUrl: String?,

    // We only need some ingredient slots (TheMealDB has up to 20)
    @SerializedName("strIngredient1") val ingredient1: String?,
    @SerializedName("strIngredient2") val ingredient2: String?,
    @SerializedName("strIngredient3") val ingredient3: String?,
    @SerializedName("strIngredient4") val ingredient4: String?,
    @SerializedName("strIngredient5") val ingredient5: String?,
    @SerializedName("strIngredient6") val ingredient6: String?,
    @SerializedName("strIngredient7") val ingredient7: String?,
    @SerializedName("strIngredient8") val ingredient8: String?,
    @SerializedName("strIngredient9") val ingredient9: String?,
    @SerializedName("strIngredient10") val ingredient10: String?
) {
    /**
     * Returns a cleaned list of ingredient names
     */
    fun ingredients(): List<String> = listOf(
        ingredient1,
        ingredient2,
        ingredient3,
        ingredient4,
        ingredient5,
        ingredient6,
        ingredient7,
        ingredient8,
        ingredient9,
        ingredient10
    )
        .mapNotNull { it?.trim() }
        .filter { it.isNotEmpty() }
}
