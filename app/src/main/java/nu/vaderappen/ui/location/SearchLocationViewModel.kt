package nu.vaderappen.ui.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nu.vaderappen.data.service.location.LocationService

class LocationViewModel(private val locationService: LocationService) : ViewModel() {

    private val _locationState =
        MutableStateFlow<LocationUiState>(LocationUiState.Success(emptyList()))
    val locationUiState: StateFlow<LocationUiState> = _locationState.asStateFlow()

    fun searchLocation(query: String) {
        viewModelScope.launch {
            _locationState.emit(LocationUiState.Loading)
            val result = locationService.searchLocation(query)
            _locationState.emit(LocationUiState.Success(result.toUiModelLocation()))
        }
    }

    fun onLocationSelected(location: Location) {
        
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
                return LocationViewModel(locationService) as T
            }
        }
    }

}

sealed interface LocationUiState {
    data object Loading : LocationUiState
    data class Success(val locations: List<Location>) : LocationUiState
}

data class Location(
    val name: String,
    val fullName: String,
    val latitude: Double,
    val longitude: Double,
)

private fun nu.vaderappen.data.service.location.Location.toUiModelLocation() = features.map {
    Location(
        name = it.properties.name,
        fullName = it.properties.displayName,
        latitude = it.geometry.coordinates[1],
        longitude = it.geometry.coordinates[0]
    )
}