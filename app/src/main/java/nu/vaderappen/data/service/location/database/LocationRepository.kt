package nu.vaderappen.data.service.location.database

import nu.vaderappen.ui.location.Location


class LocationRepository(private val locationDao: LocationDao) {

    suspend fun insertLocation(location: Location) = locationDao.insert(location)

    suspend fun updateLocation(location: Location) = locationDao.update(location)

    fun getAllLocations() = locationDao.getAllLocations()

    fun getFavedLocations() = locationDao.getFavedLocations()

    suspend fun deleteLocations(vararg locations: Location) =
        locationDao.deleteLocations(*locations)
}