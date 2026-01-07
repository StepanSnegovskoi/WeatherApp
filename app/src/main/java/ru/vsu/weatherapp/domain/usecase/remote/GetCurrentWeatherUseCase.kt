package ru.vsu.weatherapp.domain.usecase.remote

import ru.vsu.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class GetCurrentWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {

    suspend operator fun invoke(cityId: Int) = weatherRepository.getWeather(cityId)
}