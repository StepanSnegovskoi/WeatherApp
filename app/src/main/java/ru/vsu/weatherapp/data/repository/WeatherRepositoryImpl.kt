package ru.vsu.weatherapp.data.repository

import ru.vsu.weatherapp.data.api.ApiService
import ru.vsu.weatherapp.data.mapper.toEntity
import ru.vsu.weatherapp.domain.entity.Forecast
import ru.vsu.weatherapp.domain.entity.Weather
import ru.vsu.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : WeatherRepository {

    override suspend fun getWeather(cityId: Int): Weather =
        apiService.loadCurrentWeather("$PREFIX_CITY_ID$cityId").toEntity()

    override suspend fun getForecast(cityId: Int): Forecast =
        apiService.loadForecast("$PREFIX_CITY_ID$cityId").toEntity()

    private companion object {

        private const val PREFIX_CITY_ID = "id:"
    }
}