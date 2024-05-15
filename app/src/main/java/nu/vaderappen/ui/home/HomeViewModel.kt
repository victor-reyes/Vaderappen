package nu.vaderappen.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import nu.vaderappen.data.service.location.LocationService
import nu.vaderappen.data.service.location.gps.LocationManager
import nu.vaderappen.data.service.location.toUiModelLocation
import nu.vaderappen.data.service.prefs.PrefsRepository
import nu.vaderappen.data.service.prefs.dataStore

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val prefsRepository: PrefsRepository,
    private val locationManager: LocationManager,
    locationService: LocationService,
) : ViewModel() {

    val currentLocation = prefsRepository.shouldUseGPS
        .flatMapLatest { shouldUseGPS ->
            if (shouldUseGPS) locationManager.currentLocation
                .filterNotNull()
                .map { (lat, long) ->
                    locationService.getReverseGeoCoding(lat, long)
                        .toUiModelLocation(lat, long)
                }
            else prefsRepository.location
        }
        .onEach { it?.let { prefsRepository.saveLocation(it) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setCanUseGPS(canUseGPS: Boolean) {
        locationManager.setCanUseGPS(canUseGPS)
    }

    fun onSearchLocation(){
        viewModelScope.launch { prefsRepository.setShouldUseGPS(true) }
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
                val locationManager = LocationManager
                    .getInstance(LocationServices.getFusedLocationProviderClient(application))
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