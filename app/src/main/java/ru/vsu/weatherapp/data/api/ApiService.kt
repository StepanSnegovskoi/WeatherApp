package ru.vsu.weatherapp.data.api

import ru.vsu.weatherapp.data.dto.CityDto
import retrofit2.http.GET
import retrofit2.http.Query
import ru.vsu.weatherapp.data.dto.WeatherCurrentDto
import ru.vsu.weatherapp.data.dto.WeatherForecastDto

interface ApiService {

    @GET("current.json")
    suspend fun loadCurrentWeather(
        @Query("q") query: String
    ): WeatherCurrentDto

    @GET("forecast.json")
    suspend fun loadForecast(
        @Query("q") query: String,
        @Query("days") daysCount: Int = 3
    ): WeatherForecastDto

    @GET("search.json")
    suspend fun searchCity(
        @Query("q") query: String
    ): List<CityDto>
}