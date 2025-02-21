package com.kianmahmoudi.android.shirazgard

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kianmahmoudi.android.shirazgard.data.Place
import com.kianmahmoudi.android.shirazgard.db.FavoritePlacesDao

@Database(entities = [Place::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoritePlacesDao(): FavoritePlacesDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) { // This synchronized block is okay
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}