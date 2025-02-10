package com.example.weatherapp.data.mapper

import com.example.weatherapp.data.dto.CityDto
import com.example.weatherapp.domain.entity.City

fun CityDto.toEntity() = City(id, name, country)

fun List<CityDto>.toEntities() = map { it.toEntity() }