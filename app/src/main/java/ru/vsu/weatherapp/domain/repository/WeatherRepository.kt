package ru.vsu.weatherapp.domain.repository

import ru.vsu.weatherapp.domain.entity.Forecast
import ru.vsu.weatherapp.domain.entity.Weather


interface WeatherRepository {

    suspend fun getWeather(cityId: Int): Weather

    suspend fun getForecast(cityId: Int): Forecast
}