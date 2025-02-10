package com.example.weatherapp.presentation.extensions

import kotlin.math.roundToInt

fun Float.tempToFormattedString(): String = "${roundToInt()}°C"