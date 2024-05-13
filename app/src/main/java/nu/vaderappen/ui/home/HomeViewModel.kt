package nu.vaderappen.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import nu.vaderappen.data.service.location.LocationService
import nu.vaderappen.data.service.location.gps.LocationManager
import nu.vaderappen.data.service.location.toUiModelLocation
import nu.vaderappen.data.service.prefs.PrefsRepository
import nu.vaderappen.data.service.prefs.dataStore
import nu.vaderappen.ui.location.Location
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalPermissionsApi::class)
class HomeViewModel(
    private val prefsRepository: PrefsRepository,
    private val locationManager: LocationManager,
    locationService: LocationService,
) : ViewModel() {

    private val currentLocation = locationManager.currentLocation
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
            null
        )

    val location: Flow<Location> = prefsRepository.location
        .flatMapConcat { location ->
            when {
                location == null || location.shouldUseGps -> currentLocation
                    .filterNotNull()
                    .map { it.latitude to it.longitude }
                    .map { (lat, long) ->
                        locationService.getReverseGeoCoding(lat, long)
                            .toUiModelLocation(lat, long)
                    }

                else -> flowOf(location)
            }
        }

    @OptIn(ExperimentalPermissionsApi::class)
    private val locationPermissionsState: MutableStateFlow<MultiplePermissionsState?> =
        MutableStateFlow(null)


    init {
        combine(locationPermissionsState, location) { permissionsState, location ->
            !permissionsState?.permissions.isNullOrEmpty() && location.shouldUseGps
        }.onEach { locationManager.shouldUseGps(it) }
            .launchIn(viewModelScope)

    }


    @OptIn(ExperimentalPermissionsApi::class)
    fun updateLocationPermissionsState(locationPermissionsState: MultiplePermissionsState) {
        this.locationPermissionsState.value = locationPermissionsState
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras,
            ): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                val prefsRepository = PrefsRepository(application.dataStore)
                val locationManager =
                    LocationManager(LocationServices.getFusedLocationProviderClient(application))
                val locationService = LocationService.create()
                return HomeViewModel(
                    prefsRepository = prefsRepository,
                    locationManager = locationManager,
                    locationService = locationService
                ) as T
            }
        }
    }
}