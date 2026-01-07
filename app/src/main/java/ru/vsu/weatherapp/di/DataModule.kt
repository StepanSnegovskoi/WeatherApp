package ru.vsu.weatherapp.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.vsu.weatherapp.data.api.ApiFactory
import ru.vsu.weatherapp.data.api.ApiService
import ru.vsu.weatherapp.data.local.db.FavouriteCitiesDao
import ru.vsu.weatherapp.data.local.db.FavouriteDatabase
import ru.vsu.weatherapp.data.local.db.FavouriteDatabase.Companion.DB_NAME
import ru.vsu.weatherapp.data.repository.FavouriteRepositoryImpl
import ru.vsu.weatherapp.data.repository.SearchRepositoryImpl
import ru.vsu.weatherapp.data.repository.WeatherRepositoryImpl
import ru.vsu.weatherapp.domain.repository.FavouriteRepository
import ru.vsu.weatherapp.domain.repository.SearchRepository
import ru.vsu.weatherapp.domain.repository.WeatherRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindFavouriteRepository(impl: FavouriteRepositoryImpl): FavouriteRepository

    @Singleton
    @Binds
    fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository

    @Singleton
    @Binds
    fun bindWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository

    companion object {

        @Singleton
        @Provides
        fun provideApiService(): ApiService = ApiFactory.apiService

        @Singleton
        @Provides
        fun provideFavouriteDatabase(@ApplicationContext context: Context): FavouriteDatabase =
            Room.databaseBuilder(
                context,
                FavouriteDatabase::class.java,
                DB_NAME
            ).build()

        @Singleton
        @Provides
        fun provideFavouriteCitiesDao(database: FavouriteDatabase): FavouriteCitiesDao =
            database.favouriteCitiesDao()
    }
}