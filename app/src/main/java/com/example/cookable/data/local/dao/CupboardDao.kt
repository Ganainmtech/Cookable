package com.example.cookable.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cookable.data.local.entity.CupboardItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CupboardDao {
    @Query("SELECT * FROM cupboard_items ORDER BY id DESC")
    fun getAll(): Flow<List<CupboardItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CupboardItemEntity)

    @Query("DELETE FROM cupboard_items WHERE id = :id")
    suspend fun deleteById(id: Long)
}
