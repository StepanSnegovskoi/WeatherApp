package ru.vsu.weatherapp.data.mapper

import ru.vsu.weatherapp.data.dto.WeatherCurrentDto
import ru.vsu.weatherapp.data.dto.WeatherDto
import ru.vsu.weatherapp.data.dto.WeatherForecastDto
import ru.vsu.weatherapp.domain.entity.Forecast
import ru.vsu.weatherapp.domain.entity.Weather
import java.util.Calendar
import java.util.Date

fun WeatherCurrentDto.toEntity() = current.toEntity()

fun WeatherDto.toEntity() = Weather(
    tempC = tempC,
    conditionText = condition.text,
    conditionUrl = condition.iconUrl.correctImageUrl(),
    date = date.toCalendar()
)

/**
 * Сервер возвращает прогноз на N дней в таком виде: Текущий день + прогноз на N - 1 дней,
 * текущий день нас не интересует, поэтому выкидываем его drop(1)
 */
fun WeatherForecastDto.toEntity() = Forecast(
    currentWeather = current.toEntity(),
    upcoming = forecastDto.forecastDay.drop(1).map { dayDto ->
        val dayWeatherDto = dayDto.dayWeatherDto
        Weather(
            tempC = dayWeatherDto.tempC,
            conditionText = dayWeatherDto.condition.text,
            conditionUrl = dayWeatherDto.condition.iconUrl.correctImageUrl(),
            date = dayDto.date.toCalendar(),
        )
    }
)

/**
 * Сервер возвращает не миллисекунды, а секунды, нужно преобразовывать
 */
private fun Long.toCalendar() = Calendar.getInstance().apply {
    time = Date(this@toCalendar * MILLIS_IN_SECOND)
}

private fun String.correctImageUrl() = "$PROTOCOL_HTTPS:$this".replace(
    oldValue = ICON_SIZE_64x64,
    newValue = ICON_SIZE_128x128
)

private const val MILLIS_IN_SECOND = 1000L

private const val PROTOCOL_HTTPS = "https"

private const val ICON_SIZE_64x64 = "64x64"
private const val ICON_SIZE_128x128 = "128x128"