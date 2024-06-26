package nu.vaderappen.data.service.weather

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


sealed interface Precipitation {
    val probability: Float?
    fun getShortVersion(): String

    data class MinMaxPrecipitation(
        val min: Double,
        val max: Double,
        override val probability: Float?,
    ) : Precipitation {
        override fun getShortVersion(): String {
            return "$min-$max"
        }

        override fun toString(): String {
            return "$min-$max${probability?.let { " ($it%)" } ?: ""}"
        }
    }

    data class PrecipitationAmount(
        val amount: Double,
        override val probability: Float?,
    ) : Precipitation {
        override fun getShortVersion(): String {
            return "$amount"
        }

        override fun toString(): String {
            return "$amount${probability?.let { " ($it%)" } ?: ""}"
        }
    }
}

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

val TimeSeries.symbol: WeatherSymbol?
    get() = with(data) {
        return@with when {
            (next1Hours != null) -> next1Hours.summary.weatherSymbol
            (next6Hours != null) -> next6Hours.summary.weatherSymbol
            (next12Hours != null) -> next12Hours.summary.weatherSymbol
            else -> null
        }
    }

val TimeSeries.precipitation: Precipitation?
    get() = with(data) {
        return@with when {
            (next1Hours != null) -> next1Hours.details.getMinMaxPrecipitationAmount()
            (next6Hours != null) -> next6Hours.details.getMinMaxPrecipitationAmount()
            (next12Hours != null) -> next12Hours.details.getMinMaxPrecipitationAmount()
            else -> null
        }
    }

private fun NextHoursDetails.getMinMaxPrecipitationAmount(): Precipitation? =
    if (precipitationAmountMin != null && precipitationAmountMax != null && precipitationAmountMax > 0)
        Precipitation.MinMaxPrecipitation(
            precipitationAmountMin,
            precipitationAmountMax,
            probabilityOfPrecipitation?.toFloat()
        )
    else if (precipitationAmount != null && precipitationAmount > 0)
        Precipitation.PrecipitationAmount(
            precipitationAmount,
            probabilityOfPrecipitation?.toFloat()
        )
    else null

fun LocalDate.toSimpleDate(): String {
    val today = LocalDate.now()
    val dayOfWeek = when (today.dayOfYear) {
        this.dayOfYear -> "Idag"
        this.dayOfYear - 1 -> "Imorgon"
        else -> this.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    }
    val date = "$dayOfMonth ${month.getDisplayName(TextStyle.SHORT, Locale.getDefault())}"

    return "$dayOfWeek, $date"
}