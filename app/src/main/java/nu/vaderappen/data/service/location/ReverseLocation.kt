package nu.vaderappen.data.service.location

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import nu.vaderappen.ui.location.Location

sealed interface ReverseLocation {

    @JsonClass(generateAdapter = true)
    data class Location(
        val type: String,
        val licence: String,
        val features: List<Feature>,
    ) : ReverseLocation

    @JsonClass(generateAdapter = true)
    data class Feature(
        val type: String,
        val properties: Properties,
        val bbox: List<Double>,
        @Json(name = "geometry") val geometry: Geometry,
    )

    @JsonClass(generateAdapter = true)
    data class Properties(
        @Json(name = "place_id") val placeId: String,
        @Json(name = "osm_type") val osmType: String,
        @Json(name = "osm_id") val osmId: String,
        @Json(name = "place_rank") val placeRank: Int,
        @Json(name = "category") val category: String,
        @Json(name = "type") val type: String,
        @Json(name = "importance") val importance: Double,
        @Json(name = "addresstype") val addressType: String,
        val name: String?,
        @Json(name = "display_name") val displayName: String,
        val address: Address,
    )

    @JsonClass(generateAdapter = true)
    data class Address(
        @Json(name = "house_number") val houseNumber: String,
        @Json(name = "road") val road: String,
        val suburb: String,
        val city: String,
        val county: String,
        val state: String,
        val postcode: String,
        val country: String,
        @Json(name = "country_code") val countryCode: String,
    )

    @JsonClass(generateAdapter = true)
    data class Error(
        val error: String,
    ) : ReverseLocation
}

fun ReverseLocation.toUiModelLocation(lattiude: Double, longtiude: Double) = when (this) {
    is ReverseLocation.Error -> Location(
        name = "Unknown",
        fullName = "Could not find location",
        latitude = lattiude, longitude = longtiude,
        shouldUseGps = true
    )

    is ReverseLocation.Location -> features.first().let {
        Location(
            name = it.properties.name ?: it.properties.displayName,
            fullName = it.properties.displayName,
            latitude = it.geometry.coordinates[1], longitude = it.geometry.coordinates[0],
            shouldUseGps = false
        )
    }
}



