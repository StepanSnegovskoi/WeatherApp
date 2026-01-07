package ru.vsu.weatherapp.data.repository

import ru.vsu.weatherapp.data.api.ApiService
import ru.vsu.weatherapp.data.mapper.toEntities
import ru.vsu.weatherapp.domain.entity.City
import ru.vsu.weatherapp.domain.repository.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : SearchRepository {

    override suspend fun search(query: String): List<City> =
        apiService.searchCity(query).toEntities()
}