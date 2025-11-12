package com.example.cookable.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Minimal recipe fields from TheMealDB API.
 * Endpoint example: https://www.themealdb.com/api/json/v1/1/search.php?s=chicken
 */
data class RecipeSearchResponse(
    @SerializedName("meals") val meals: List<RecipeDto>?
)

data class RecipeDto(
    @SerializedName("idMeal") val id: String,
    @SerializedName("strMeal") val name: String,
    @SerializedName("strInstructions") val instructions: String?,
    @SerializedName("strMealThumb") val thumbnailUrl: String?
)
