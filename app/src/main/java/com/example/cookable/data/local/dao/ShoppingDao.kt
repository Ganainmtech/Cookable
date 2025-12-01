package com.example.cookable.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cookable.data.local.entity.ShoppingItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for the shopping list table.
 */
@Dao
interface ShoppingDao {

    // Return all shopping items as a Flow so UI refreshes automatically
    @Query("SELECT * FROM shopping_items ORDER BY id DESC")
    fun getAll(): Flow<List<ShoppingItemEntity>>

    // Add a new shopping item
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ShoppingItemEntity)

    // Delete a shopping item by its ID
    @Query("DELETE FROM shopping_items WHERE id = :id")
    suspend fun deleteById(id: Long)
}
