package com.example.weatherapp.data.dto

import com.google.gson.annotations.SerializedName

data class WeatherDto(
    @SerializedName("last_updated_epoch") val date: Long,
    @SerializedName("tempC") val tempC: Float,
    @SerializedName("condition") val condition: ConditionDto,
)
