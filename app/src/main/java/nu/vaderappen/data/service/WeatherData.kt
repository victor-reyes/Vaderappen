package nu.vaderappen.data.service

import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson
import nu.vaderappen.R
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId

@JsonClass(generateAdapter = true)
data class WeatherData(
    @Json(name = "type") val type: String,
    @Json(name = "geometry") val geometry: Geometry,
    @Json(name = "properties") val properties: Properties
)

@JsonClass(generateAdapter = true)
data class Geometry(
    @Json(name = "type") val type: String,
    @Json(name = "coordinates") val coordinates: List<Double>
)

@JsonClass(generateAdapter = true)
data class Properties(
    @Json(name = "meta") val meta: Meta,
    @Json(name = "timeseries") val timeseries: List<TimeSeries>
)

@JsonClass(generateAdapter = true)
data class Meta(
    @Json(name = "updated_at") val updatedAt: LocalDateTime,
    @Json(name = "units") val units: Units
)

@JsonClass(generateAdapter = true)
data class Units(
    @Json(name = "air_pressure_at_sea_level") val airPressureAtSeaLevel: String,
    @Json(name = "air_temperature") val airTemperature: String,
    @Json(name = "cloud_area_fraction") val cloudAreaFraction: String,
    @Json(name = "precipitation_amount") val precipitationAmount: String,
    @Json(name = "relative_humidity") val relativeHumidity: String,
    @Json(name = "wind_from_direction") val windFromDirection: String,
    @Json(name = "wind_speed") val windSpeed: String
)

@JsonClass(generateAdapter = true)
data class TimeSeries(
    @Json(name = "time") val time: LocalDateTime,
    @Json(name = "data") val data: WeatherDataDetails
)

@JsonClass(generateAdapter = true)
data class WeatherDataDetails(
    @Json(name = "instant") val instant: InstantWeatherDetails,
    @Json(name = "next_12_hours") val next12Hours: NextHoursWeatherDetails?,
    @Json(name = "next_1_hours") val next1Hours: NextHoursWeatherDetails?,
    @Json(name = "next_6_hours") val next6Hours: NextHoursWeatherDetails?
)

@JsonClass(generateAdapter = true)
data class InstantWeatherDetails(
    @Json(name = "details") val details: InstantDetails
)

@JsonClass(generateAdapter = true)
data class NextHoursWeatherDetails(
    @Json(name = "summary") val summary: Summary,
    @Json(name = "details") val details: NextHoursDetails
)

@JsonClass(generateAdapter = true)
data class Summary(
    @Json(name = "symbol_code") val weatherSymbol: WeatherSymbol
)

@JsonClass(generateAdapter = true)
data class InstantDetails(
    @Json(name = "air_pressure_at_sea_level") val airPressureAtSeaLevel: Double,
    @Json(name = "air_temperature") val airTemperature: Double,
    @Json(name = "cloud_area_fraction") val cloudAreaFraction: Double,
    @Json(name = "relative_humidity") val relativeHumidity: Double,
    @Json(name = "wind_from_direction") val windFromDirection: Double,
    @Json(name = "wind_speed") val windSpeed: Double
)

@JsonClass(generateAdapter = true)
data class NextHoursDetails(
    @Json(name = "precipitation_amount") val precipitationAmount: Double?
)

enum class WeatherSymbol(val drawableId: Int) {
    clearsky_day(R.drawable.clearsky_day),
    clearsky_night(R.drawable.clearsky_night),
    clearsky_polartwilight(R.drawable.clearsky_polartwilight),
    fair_day(R.drawable.fair_day),
    fair_night(R.drawable.fair_night),
    fair_polartwilight(R.drawable.fair_polartwilight),
    lightssnowshowersandthunder_day(R.drawable.lightssnowshowersandthunder_day),
    lightssnowshowersandthunder_night(R.drawable.lightssnowshowersandthunder_night),
    lightssnowshowersandthunder_polartwilight(R.drawable.lightssnowshowersandthunder_polartwilight),
    lightsnowshowers_day(R.drawable.lightsnowshowers_day),
    lightsnowshowers_night(R.drawable.lightsnowshowers_night),
    lightsnowshowers_polartwilight(R.drawable.lightsnowshowers_polartwilight),
    heavyrainandthunder(R.drawable.heavyrainandthunder),
    heavysnowandthunder(R.drawable.heavysnowandthunder),
    rainandthunder(R.drawable.rainandthunder),
    heavysleetshowersandthunder_day(R.drawable.heavysleetshowersandthunder_day),
    heavysleetshowersandthunder_night(R.drawable.heavysleetshowersandthunder_night),
    heavysleetshowersandthunder_polartwilight(R.drawable.heavysleetshowersandthunder_polartwilight),
    heavysnow(R.drawable.heavysnow),
    heavyrainshowers_day(R.drawable.heavyrainshowers_day),
    heavyrainshowers_night(R.drawable.heavyrainshowers_night),
    heavyrainshowers_polartwilight(R.drawable.heavyrainshowers_polartwilight),
    lightsleet(R.drawable.lightsleet),
    heavyrain(R.drawable.heavyrain),
    lightrainshowers_day(R.drawable.lightrainshowers_day),
    lightrainshowers_night(R.drawable.lightrainshowers_night),
    lightrainshowers_polartwilight(R.drawable.lightrainshowers_polartwilight),
    heavysleetshowers_day(R.drawable.heavysleetshowers_day),
    heavysleetshowers_night(R.drawable.heavysleetshowers_night),
    heavysleetshowers_polartwilight(R.drawable.heavysleetshowers_polartwilight),
    lightsleetshowers_day(R.drawable.lightsleetshowers_day),
    lightsleetshowers_night(R.drawable.lightsleetshowers_night),
    lightsleetshowers_polartwilight(R.drawable.lightsleetshowers_polartwilight),
    snow(R.drawable.snow),
    heavyrainshowersandthunder_day(R.drawable.heavyrainshowersandthunder_day),
    heavyrainshowersandthunder_night(R.drawable.heavyrainshowersandthunder_night),
    heavyrainshowersandthunder_polartwilight(R.drawable.heavyrainshowersandthunder_polartwilight),
    snowshowers_day(R.drawable.snowshowers_day),
    snowshowers_night(R.drawable.snowshowers_night),
    snowshowers_polartwilight(R.drawable.snowshowers_polartwilight),
    fog(R.drawable.fog),
    snowshowersandthunder_day(R.drawable.snowshowersandthunder_day),
    snowshowersandthunder_night(R.drawable.snowshowersandthunder_night),
    snowshowersandthunder_polartwilight(R.drawable.snowshowersandthunder_polartwilight),
    lightsnowandthunder(R.drawable.lightsnowandthunder),
    heavysleetandthunder(R.drawable.heavysleetandthunder),
    lightrain(R.drawable.lightrain),
    rainshowersandthunder_day(R.drawable.rainshowersandthunder_day),
    rainshowersandthunder_night(R.drawable.rainshowersandthunder_night),
    rainshowersandthunder_polartwilight(R.drawable.rainshowersandthunder_polartwilight),
    rain(R.drawable.rain),
    lightsnow(R.drawable.lightsnow),
    lightrainshowersandthunder_day(R.drawable.lightrainshowersandthunder_day),
    lightrainshowersandthunder_night(R.drawable.lightrainshowersandthunder_night),
    lightrainshowersandthunder_polartwilight(R.drawable.lightrainshowersandthunder_polartwilight),
    heavysleet(R.drawable.heavysleet),
    sleetandthunder(R.drawable.sleetandthunder),
    lightrainandthunder(R.drawable.lightrainandthunder),
    sleet(R.drawable.sleet),
    lightssleetshowersandthunder_day(R.drawable.lightssleetshowersandthunder_day),
    lightssleetshowersandthunder_night(R.drawable.lightssleetshowersandthunder_night),
    lightssleetshowersandthunder_polartwilight(R.drawable.lightssleetshowersandthunder_polartwilight),
    lightsleetandthunder(R.drawable.lightsleetandthunder),
    partlycloudy_day(R.drawable.partlycloudy_day),
    partlycloudy_night(R.drawable.partlycloudy_night),
    partlycloudy_polartwilight(R.drawable.partlycloudy_polartwilight),
    sleetshowersandthunder_day(R.drawable.sleetshowersandthunder_day),
    sleetshowersandthunder_night(R.drawable.sleetshowersandthunder_night),
    sleetshowersandthunder_polartwilight(R.drawable.sleetshowersandthunder_polartwilight),
    rainshowers_day(R.drawable.rainshowers_day),
    rainshowers_night(R.drawable.rainshowers_night),
    rainshowers_polartwilight(R.drawable.rainshowers_polartwilight),
    snowandthunder(R.drawable.snowandthunder),
    sleetshowers_day(R.drawable.sleetshowers_day),
    sleetshowers_night(R.drawable.sleetshowers_night),
    sleetshowers_polartwilight(R.drawable.sleetshowers_polartwilight),
    cloudy(R.drawable.cloudy),
    heavysnowshowersandthunder_day(R.drawable.heavysnowshowersandthunder_day),
    heavysnowshowersandthunder_night(R.drawable.heavysnowshowersandthunder_night),
    heavysnowshowersandthunder_polartwilight(R.drawable.heavysnowshowersandthunder_polartwilight),
    heavysnowshowers_day(R.drawable.heavysnowshowers_day),
    heavysnowshowers_night(R.drawable.heavysnowshowers_night),
    heavysnowshowers_polartwilight(R.drawable.heavysnowshowers_polartwilight)
}


class LocaleDateTimeAdapter {

    @FromJson
    fun fromJson(date: String): LocalDateTime = OffsetDateTime
        .parse(date)
        .atZoneSameInstant(ZoneId.systemDefault())
        .toLocalDateTime()

    @ToJson
    fun toJson(date: LocalDateTime): String = date.toString()
}