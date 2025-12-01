package com.example.cookable.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Item stored in the user's cupboard
 * Each entry can optionally include quantity + unit
 */
@Entity(tableName = "cupboard_items")
data class CupboardItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L, // auto-generates the ID
    val name: String,        // e.g, "Tomato"
    val quantity: String? = null, // optional amount
    val unit: String? = null      // optional unit (g, ml, x, etc)
)
