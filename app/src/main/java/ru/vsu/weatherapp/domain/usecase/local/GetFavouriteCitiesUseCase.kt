package ru.vsu.weatherapp.domain.usecase.local

import ru.vsu.weatherapp.domain.repository.FavouriteRepository
import javax.inject.Inject

class GetFavouriteCitiesUseCase @Inject constructor(
    private val favouriteRepository: FavouriteRepository
) {

    operator fun invoke() = favouriteRepository.favouriteCities
}