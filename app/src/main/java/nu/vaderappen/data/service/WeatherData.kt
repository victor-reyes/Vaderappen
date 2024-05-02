package nu.vaderappen.data.service

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

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
    @Json(name = "updated_at") val updatedAt: String,
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
    @Json(name = "time") val time: String,
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
    @Json(name = "symbol_code") val symbolCode: String
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

