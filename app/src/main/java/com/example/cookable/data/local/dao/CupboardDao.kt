package com.example.cookable.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cookable.data.local.entity.CupboardItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO for cupboard items.
 */
@Dao
interface CupboardDao {

    // Get all cupboard items (Flow updates the UI automatically)
    @Query("SELECT * FROM cupboard_items ORDER BY id DESC")
    fun getAll(): Flow<List<CupboardItemEntity>>

    // Add a new cupboard item
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CupboardItemEntity)

    // Remove one by ID
    @Query("DELETE FROM cupboard_items WHERE id = :id")
    suspend fun deleteById(id: Long)
}
