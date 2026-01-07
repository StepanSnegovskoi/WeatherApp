package ru.vsu.weatherapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.vsu.weatherapp.data.local.model.CityDbModel

@Database(entities = [CityDbModel::class], version = 1, exportSchema = false)
abstract class FavouriteDatabase : RoomDatabase() {

    abstract fun favouriteCitiesDao(): FavouriteCitiesDao

    companion object {

        const val DB_NAME = "FavouriteDatabase"
    }
}