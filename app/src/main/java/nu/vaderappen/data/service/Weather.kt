package nu.vaderappen.data.service

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale

data class Weather(
    val updateAt: LocalDateTime,
    val coordinates: List<Double>,
    val forecastByDay: List<Day>,
)


data class Day(
    val date: String,
    val forecast: List<TimeSeries>,
    val units: Units,
)


fun WeatherData.toWeather(): Weather {
    val updateAt = properties.meta.updatedAt
    val coordinates = geometry.coordinates
    val units = properties.meta.units
    val forecastByDay = properties.timeseries
        .groupBy { it.time.toLocalDate() }
        .toSortedMap()
        .map { (date, forecast) ->
            Day(date.toSimpleDate(), forecast, units)
        }

    return Weather(updateAt, coordinates, forecastByDay)
}

fun TimeSeries.getSymbol(): WeatherSymbol? = with(data) {
    return@with when {
        (next1Hours != null) -> next1Hours.summary.weatherSymbol
        (next6Hours != null) -> next6Hours.summary.weatherSymbol
        (next12Hours != null) -> next12Hours.summary.weatherSymbol
        else -> null
    }
}

fun TimeSeries.getPrecipitationAmount(): Double = with(data) {
    return@with when {
        (next1Hours != null) -> next1Hours.details.precipitationAmount ?: 0.0
        (next6Hours != null) -> next6Hours.details.precipitationAmount ?: 0.0
        (next12Hours != null) -> next12Hours.details.precipitationAmount ?: 0.0
        else -> 0.0
    }
}

private fun LocalDate.toSimpleDate() =
    "${
        dayOfWeek.getDisplayName(
            TextStyle.SHORT,
            Locale.getDefault()
        )
    }, $dayOfMonth ${month.getDisplayName(TextStyle.SHORT, Locale.getDefault())}"