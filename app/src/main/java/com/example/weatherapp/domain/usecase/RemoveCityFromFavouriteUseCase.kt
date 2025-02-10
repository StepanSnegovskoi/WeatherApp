package com.example.weatherapp.domain.usecase

import com.example.weatherapp.domain.repository.FavouriteRepository
import javax.inject.Inject

class RemoveCityFromFavouriteUseCase @Inject constructor(
    private val favouriteRepository: FavouriteRepository
) {

    suspend operator fun invoke(cityId: Int) = favouriteRepository.removeFromFavourite(cityId)
}