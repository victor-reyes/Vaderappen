package nu.vaderappen.ui.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nu.vaderappen.data.service.Weather
import nu.vaderappen.data.service.YrService
import nu.vaderappen.data.service.format
import nu.vaderappen.data.service.toWeather
import nu.vaderappen.ui.forecast.ForecastUi.Forecast
import nu.vaderappen.ui.forecast.ForecastUi.Loading
import kotlin.random.Random

class ForecastViewModel(
    val yr: YrService,
) : ViewModel() {

    private val _forecastUi: MutableStateFlow<ForecastUi> = MutableStateFlow(Loading)
    val forecastUi = _forecastUi.asStateFlow()

    init {
        viewModelScope.launch {
            val r = Random

            val latitude = (r.nextDouble() * 180 - 90).format(3);
            val longitude = (r.nextDouble() * 360 - 180).format(3)
            val barcelona = 41.390205 to 2.154007
            val stockholm = 59.3103 to 18.1036
            val weather = yr.getWeatherData(stockholm.first, stockholm.second)
                .toWeather()
            _forecastUi.emit(Forecast(weather))
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
                val yr = YrService.create()


                return ForecastViewModel(yr) as T
            }
        }
    }
}


sealed interface ForecastUi {
    data object Loading : ForecastUi
    data class Forecast(val weather: Weather) : ForecastUi
}