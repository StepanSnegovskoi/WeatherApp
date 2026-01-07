package ru.vsu.weatherapp.data.mapper

import ru.vsu.weatherapp.data.dto.CityDto
import ru.vsu.weatherapp.domain.entity.City

fun CityDto.toEntity() = City(id, name, country)

fun List<CityDto>.toEntities() = map { it.toEntity() }