package ru.vsu.weatherapp.domain.usecase.local

import ru.vsu.weatherapp.domain.repository.FavouriteRepository
import javax.inject.Inject

class ObserveFavouriteStateUseCase @Inject constructor(
    private val favouriteRepository: FavouriteRepository
) {

    operator fun invoke(cityId: Int) = favouriteRepository.observeIsFavourite(cityId)
}