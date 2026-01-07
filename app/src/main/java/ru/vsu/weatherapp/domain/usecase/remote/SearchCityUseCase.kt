package ru.vsu.weatherapp.domain.usecase.remote

import ru.vsu.weatherapp.domain.repository.SearchRepository
import javax.inject.Inject

class SearchCityUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {

    suspend operator fun invoke(name: String) = searchRepository.search(name)
}