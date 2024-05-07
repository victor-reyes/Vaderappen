package nu.vaderappen.data.service.location.database

import nu.vaderappen.ui.location.Location


class LocationRepository(private val locationDao: LocationDao) {

    suspend fun upsert(location: Location) = locationDao.upsert(location)

    fun getAllLocations() = locationDao.getAllLocations()

    fun getFavedLocations() = locationDao.getFavedLocations()

    suspend fun delete(vararg locations: Location) =
        locationDao.delete(*locations)
}