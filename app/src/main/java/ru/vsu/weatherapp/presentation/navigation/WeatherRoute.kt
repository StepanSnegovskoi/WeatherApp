package ru.vsu.weatherapp.presentation.navigation

import ru.vsu.weatherapp.domain.entity.City
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import ru.vsu.weatherapp.presentation.search.OpenReason

@Serializable
sealed class WeatherRoute : NavKey {
    @Serializable
    data object Favourite : WeatherRoute()

    @Serializable
    data class Search(val reason: OpenReason) : WeatherRoute()

    @Serializable
    data class Details(val city: City, val index: Int) : WeatherRoute()
}