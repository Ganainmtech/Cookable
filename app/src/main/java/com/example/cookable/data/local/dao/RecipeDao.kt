package com.example.cookable.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cookable.data.local.entity.RecipeEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for the local recipe table (the 3 built-in recipes).
 */
@Dao
interface RecipeDao {

    // Get all local recipes as a Flow so UI updates automatically
    @Query("SELECT * FROM recipes")
    fun getAll(): Flow<List<RecipeEntity>>

    // Get one recipe by ID
    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getById(id: Long): RecipeEntity?

    // Insert a single recipe
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipe: RecipeEntity)

    // Insert multiple (used when seeding the DB)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(recipes: List<RecipeEntity>)
}
