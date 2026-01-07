package ru.vsu.weatherapp.data.repository

import ru.vsu.weatherapp.data.local.db.FavouriteCitiesDao
import ru.vsu.weatherapp.domain.entity.City
import ru.vsu.weatherapp.domain.repository.FavouriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.vsu.weatherapp.data.mapper.toDbModel
import ru.vsu.weatherapp.data.mapper.toEntities
import javax.inject.Inject

class FavouriteRepositoryImpl @Inject constructor(
    private val favouriteCitiesDao: FavouriteCitiesDao
) : FavouriteRepository {

    override val favouriteCities: Flow<List<City>> = favouriteCitiesDao.getFavouriteCities()
        .map { it.toEntities() }

    override fun observeIsFavourite(cityId: Int): Flow<Boolean> =
        favouriteCitiesDao.observeIsFavourite(cityId)

    override suspend fun addToFavourite(city: City) {
        favouriteCitiesDao.addCityToFavourite(city.toDbModel())
    }

    override suspend fun removeFromFavourite(cityId: Int) {
        favouriteCitiesDao.removeCityFromFavourite(cityId)
    }
}