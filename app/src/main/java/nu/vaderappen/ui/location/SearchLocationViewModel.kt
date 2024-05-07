package nu.vaderappen.ui.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import nu.vaderappen.data.service.location.LocationService
import nu.vaderappen.data.service.location.database.LocationDatabase
import nu.vaderappen.data.service.location.database.LocationRepository
import nu.vaderappen.data.service.prefs.PrefsRepository
import nu.vaderappen.data.service.prefs.dataStore

class LocationViewModel(
    private val locationService: LocationService,
    private val locationRepository: LocationRepository,
    private val prefsRepository: PrefsRepository,
) : ViewModel() {
    val locationUiState: StateFlow<LocationUiState>

    private val searchedLocations = MutableStateFlow<List<Location>>(emptyList())

    init {
        val currentLocation =
            prefsRepository.location.stateIn(viewModelScope, SharingStarted.Lazily, null)
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
            searchedLocations.emit(result.toUiModelLocation())
        }
    }

    fun onFavedChange(location: Location, isFaved: Boolean) {
        viewModelScope.launch {
            locationRepository.upsert(location.copy(isFaved = isFaved))
        }
    }

    fun onLocationSelected(location: Location) {
        searchedLocations.value = emptyList()
        viewModelScope.launch {
            prefsRepository.saveLocation(location)
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
                return LocationViewModel(
                    locationService = locationService,
                    locationRepository = locationRepository,
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
    @PrimaryKey
    val fullName: String,
    val latitude: Double,
    val longitude: Double,
    val isFaved: Boolean = false,
)

private fun nu.vaderappen.data.service.location.Location.toUiModelLocation() = features.map {
    Location(
        name = it.properties.name,
        fullName = it.properties.displayName,
        latitude = it.geometry.coordinates[1],
        longitude = it.geometry.coordinates[0]
    )
}