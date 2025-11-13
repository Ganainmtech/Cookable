package com.example.cookable.data.remote.api

import com.example.cookable.data.local.dao.RecipeSearchResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit interface for TheMealDB.
 *
 * Example request:
 *  GET https://www.themealdb.com/api/json/v1/1/search.php?s=chicken
 */
interface RecipeApi {

    @GET("api/json/v1/1/search.php")
    suspend fun searchRecipes(
        @Query("s") query: String
    ): RecipeSearchResponse

    companion object {
        private const val BASE_URL = "https://www.themealdb.com/"

        // Very simple singleton Retrofit instance.
        val instance: RecipeApi by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RecipeApi::class.java)
        }
    }
}
