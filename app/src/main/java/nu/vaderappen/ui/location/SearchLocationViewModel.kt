package nu.vaderappen.ui.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.android.gms.location.LocationServices
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import nu.vaderappen.data.service.location.LocationService
import nu.vaderappen.data.service.location.database.LocationDatabase
import nu.vaderappen.data.service.location.database.LocationRepository
import nu.vaderappen.data.service.location.gps.LocationManager
import nu.vaderappen.data.service.location.toUiModelLocation
import nu.vaderappen.data.service.prefs.PrefsRepository
import nu.vaderappen.data.service.prefs.dataStore

class LocationViewModel(
    private val locationService: LocationService,
    private val locationRepository: LocationRepository,
    private val locationManager: LocationManager,
    private val prefsRepository: PrefsRepository,
) : ViewModel() {
    val locationUiState: StateFlow<LocationUiState>

    private val searchedLocations = MutableStateFlow<List<Location>>(emptyList())

    init {
        val currentLocation = locationManager.currentLocation
            .map {
                it?.let {
                    locationService
                        .getReverseGeoCoding(it.latitude, it.longitude)
                        .toUiModelLocation(it.latitude, it.longitude)
                }
            }
        val favedLocations = locationRepository.getFavedLocations()
        locationUiState = combine(
            searchedLocations,
            favedLocations,
            currentLocation
        ) { searched, faved, current ->
            LocationUiState.Success(
                current?.copy(isFaved = faved.find { current.fullName == it.fullName } != null),
                searched.map { loc -> loc.copy(isFaved = faved.find { loc.fullName == it.fullName } != null) },
                faved
            )
        }.stateIn(viewModelScope, SharingStarted.Lazily, LocationUiState.Loading)
    }

    fun searchLocation(query: String) {
        viewModelScope.launch {
//            _locationState.emit(LocationUiState.Loading)
            val result = locationService.searchLocation(query)
                .toUiModelLocation()
                .toSet()
                .toList()
            searchedLocations.emit(result)
        }
    }

    fun onFavedChange(location: Location, isFaved: Boolean) {
        viewModelScope.launch {
            if (isFaved)
                locationRepository.upsert(location.copy(isFaved = isFaved))
            else {
                locationRepository.delete(location)
            }
        }
    }

    fun onLocationSelected(location: Location, isGps: Boolean) {
        searchedLocations.value = emptyList()
        viewModelScope.launch {
            prefsRepository.saveLocation(location.copy(shouldUseGps = isGps))
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras,
            ): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[APPLICATION_KEY])
                val locationService = LocationService.create()
                val database = LocationDatabase.getInstance(application)
                val locationRepository = LocationRepository(database.locationDao())
                val prefsRepository = PrefsRepository(application.dataStore)
                val locationManager =
                    LocationManager(LocationServices.getFusedLocationProviderClient(application))
                return LocationViewModel(
                    locationService = locationService,
                    locationRepository = locationRepository,
                    locationManager = locationManager,
                    prefsRepository = prefsRepository,
                ) as T
            }
        }
    }

}

sealed interface LocationUiState {
    data object Loading : LocationUiState
    data class Success(
        val currentLocation: Location?,
        val searchedLocations: List<Location>,
        val favedLocations: List<Location>,
    ) : LocationUiState
}

@JsonClass(generateAdapter = true)
@Entity
data class Location(
    val name: String,
    @PrimaryKey val fullName: String,
    val latitude: Double,
    val longitude: Double,
    val isFaved: Boolean = false,
    @Ignore val shouldUseGps: Boolean = false,
) {
    constructor(
        name: String,
        fullName: String,
        latitude: Double,
        longitude: Double,
        isFaved: Boolean,
    ) : this(
        name = name,
        fullName = fullName,
        latitude = latitude,
        longitude = longitude,
        isFaved = isFaved,
        shouldUseGps = false
    )
}


private fun nu.vaderappen.data.service.location.Location.toUiModelLocation() = features.map {
    Location(
        name = it.properties.name,
        fullName = it.properties.displayName,
        latitude = it.geometry.coordinates[1],
        longitude = it.geometry.coordinates[0],
        shouldUseGps = false
    )
}