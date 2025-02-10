package com.example.weatherapp.domain.usecase

import com.example.weatherapp.domain.entity.City
import com.example.weatherapp.domain.repository.FavouriteRepository
import javax.inject.Inject

class AddCityToFavouriteUseCase @Inject constructor(
    private val favouriteRepository: FavouriteRepository
) {

    suspend operator fun invoke(city: City) = favouriteRepository.addToFavourite(city)
}