package com.example.weatherapp.domain.usecase

import com.example.weatherapp.domain.repository.SearchRepository
import javax.inject.Inject

class SearchCityUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {

    suspend operator fun invoke(name: String) = searchRepository.search(name)
}