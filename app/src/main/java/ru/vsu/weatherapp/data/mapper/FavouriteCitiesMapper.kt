package ru.vsu.weatherapp.data.mapper

import ru.vsu.weatherapp.data.local.model.CityDbModel
import ru.vsu.weatherapp.domain.entity.City

fun City.toDbModel() = CityDbModel(id, name, country)

fun CityDbModel.toEntity() = City(id, name, country)

fun List<CityDbModel>.toEntities(): List<City> = map { it.toEntity() }