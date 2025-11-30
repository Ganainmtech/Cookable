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

@Database(
    entities = [
        CupboardItemEntity::class,
        ShoppingItemEntity::class,
        RecipeEntity::class
    ],
    version = 4, // bump version so Room recreates tables
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cupboardDao(): CupboardDao
    abstract fun shoppingDao(): ShoppingDao
    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cookable.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
