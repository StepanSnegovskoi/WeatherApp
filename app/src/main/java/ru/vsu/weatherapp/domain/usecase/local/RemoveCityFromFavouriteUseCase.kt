package ru.vsu.weatherapp.domain.usecase.local

import ru.vsu.weatherapp.domain.repository.FavouriteRepository
import javax.inject.Inject

class RemoveCityFromFavouriteUseCase @Inject constructor(
    private val favouriteRepository: FavouriteRepository
) {

    suspend operator fun invoke(cityId: Int) = favouriteRepository.removeFromFavourite(cityId)
}