package com.example.weatherapp.domain.usecase

import com.example.weatherapp.domain.repository.FavouriteRepository
import javax.inject.Inject

class GetFavouriteCitiesUseCase @Inject constructor(
    private val favouriteRepository: FavouriteRepository
) {

    operator fun invoke() = favouriteRepository.favouriteCities
}