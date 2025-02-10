package com.example.weatherapp.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherapp.data.local.model.CityDbModel

@Database(entities = [CityDbModel::class], version = 1, exportSchema = false)
abstract class FavouriteDatabase : RoomDatabase() {

    abstract fun favouriteCitiesDao(): FavouriteCitiesDao

    companion object {

        private const val DB_NAME = "FavouriteDatabase"
        private var INSTANCE: FavouriteDatabase? = null
        private val LOCK = Any()

        fun getInstance(context: Context): FavouriteDatabase {
            INSTANCE?.let { return it }

            synchronized(LOCK) {
                INSTANCE?.let { return it }

                Room.databaseBuilder(
                    context,
                    FavouriteDatabase::class.java,
                    DB_NAME
                ).build().also { database ->
                    INSTANCE = database
                    return database
                }
            }
        }
    }
}