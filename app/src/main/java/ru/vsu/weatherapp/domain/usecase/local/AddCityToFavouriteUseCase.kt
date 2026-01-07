package ru.vsu.weatherapp.domain.usecase.local

import ru.vsu.weatherapp.domain.entity.City
import ru.vsu.weatherapp.domain.repository.FavouriteRepository
import javax.inject.Inject

class AddCityToFavouriteUseCase @Inject constructor(
    private val favouriteRepository: FavouriteRepository
) {

    suspend operator fun invoke(city: City) = favouriteRepository.addToFavourite(city)
}