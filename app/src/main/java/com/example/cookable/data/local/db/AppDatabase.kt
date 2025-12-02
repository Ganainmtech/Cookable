package com.example.cookable.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cookable.data.local.dao.CupboardDao
import com.example.cookable.data.local.dao.ShoppingDao
import com.example.cookable.data.local.entity.CupboardItemEntity
import com.example.cookable.data.local.entity.ShoppingItemEntity

/**
 * Main Room database for the app.
 * Holds the two tables the app actually uses:
 * - Cupboard items
 * - Shopping list items
 */
@Database(
    entities = [
        CupboardItemEntity::class,
        ShoppingItemEntity::class
    ],
    version = 5, // new version
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // DAO access points
    abstract fun cupboardDao(): CupboardDao
    abstract fun shoppingDao(): ShoppingDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        /**
         * Singleton instance of the database
         * Ensures only one db is created
         */
        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cookable.db"
                )
                    .fallbackToDestructiveMigration() // updates on schema change
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
