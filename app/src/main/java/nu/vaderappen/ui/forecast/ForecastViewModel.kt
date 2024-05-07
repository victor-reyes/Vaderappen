package nu.vaderappen.ui.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import nu.vaderappen.data.service.prefs.PrefsRepository
import nu.vaderappen.data.service.prefs.dataStore
import nu.vaderappen.data.service.weather.Weather
import nu.vaderappen.data.service.weather.YrService
import nu.vaderappen.data.service.weather.toWeather
import nu.vaderappen.ui.forecast.ForecastUi.Loading

class ForecastViewModel(
    val yr: YrService,
    val prefsRepository: PrefsRepository,
) : ViewModel() {

    val forecastUi = prefsRepository.location.map {
        val weather = yr.getWeatherData(
            latitude = it.latitude,
            longitude = it.longitude
        )
            .toWeather()
        ForecastUi.Success(weather)
    }.stateIn(viewModelScope, SharingStarted.Lazily, Loading)

    /*init {
        viewModelScope.launch {
            val r = Random

            val latitude = (r.nextDouble() * 180 - 90).format(3);
            val longitude = (r.nextDouble() * 360 - 180).format(3)
            val barcelona = 41.390205 to 2.154007
            val stockholm = 59.31035436097886 to 18.10362393099789
            val weather = yr.getWeatherData(stockholm.first, stockholm.second).toWeather()
            _forecastUi.emit(Forecast(weather))
        }
    }*/

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras,
            ): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[APPLICATION_KEY])
                val yr = YrService.create()
                val prefsRepository = PrefsRepository(application.dataStore)
                return ForecastViewModel(
                    yr = yr,
                    prefsRepository = prefsRepository
                ) as T
            }
        }
    }
}


sealed interface ForecastUi {
    data object Loading : ForecastUi
    data class Success(val weather: Weather) : ForecastUi
}