package nu.vaderappen.data.service.location

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class Location(
    val type: String,
    val licence: String,
    @Json(name = "features") val features: List<Feature>,
)

@JsonClass(generateAdapter = true)
data class Feature(
    val type: String,
    @Json(name = "properties") val properties: Properties,
    @Json(name = "bbox") val boundingBox: List<Double>,
    val geometry: Geometry,
)

@JsonClass(generateAdapter = true)
data class Properties(
    @Json(name = "place_id") val placeId: Long,
    @Json(name = "osm_type") val osmType: String,
    @Json(name = "osm_id") val osmId: Long,
    @Json(name = "place_rank") val placeRank: Int,
    val category: String,
    val type: String,
    val importance: Double,
    @Json(name = "addresstype") val addressType: String,
    val name: String,
    @Json(name = "display_name") val displayName: String,
)

@JsonClass(generateAdapter = true)
data class Geometry(
    val type: String,
    @Json(name = "coordinates") val coordinates: List<Double>,
)