package com.example.cookable.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a single item in the shopping list.
 * Quantity + unit are optional
 */
@Entity(tableName = "shopping_items")
data class ShoppingItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val quantity: String? = null,
    val unit: String? = null
)
