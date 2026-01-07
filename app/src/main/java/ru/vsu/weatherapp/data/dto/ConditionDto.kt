package ru.vsu.weatherapp.data.dto

import com.google.gson.annotations.SerializedName

data class ConditionDto(
    @SerializedName("text") val text: String,
    @SerializedName("icon") val iconUrl: String
)
