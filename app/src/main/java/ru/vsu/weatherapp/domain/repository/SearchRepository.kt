package ru.vsu.weatherapp.domain.repository

import ru.vsu.weatherapp.domain.entity.City

interface SearchRepository {

    suspend fun search(query: String): List<City>
}