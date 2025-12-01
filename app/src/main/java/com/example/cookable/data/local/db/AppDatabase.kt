package com.example.cookable.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cookable.data.local.dao.CupboardDao
import com.example.cookable.data.local.dao.RecipeDao
import com.example.cookable.data.local.dao.ShoppingDao
import com.example.cookable.data.local.entity.CupboardItemEntity
import com.example.cookable.data.local.entity.RecipeEntity
import com.example.cookable.data.local.entity.ShoppingItemEntity

/**
 * Main Room database for the app.
 * Holds three tables: cupboard, shopping list, and local recipes.
 */
@Database(
    entities = [
        CupboardItemEntity::class,
        ShoppingItemEntity::class,
        RecipeEntity::class
    ],
    version = 4, // bumped when DB structure changed
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // DAO access points
    abstract fun cupboardDao(): CupboardDao
    abstract fun shoppingDao(): ShoppingDao
    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        /**
         * Singleton instance of the database
         * makes sure the app only creates the db once
         */
        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cookable.db"
                )
                    .fallbackToDestructiveMigration() // easy reset during development
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
