package nu.vaderappen.data.service.location.gps

import android.annotation.SuppressLint
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.tasks.await
import kotlin.time.Duration.Companion.minutes

@SuppressLint("MissingPermission")
class LocationManager private constructor(locationClient: FusedLocationProviderClient) {
    private val canUseGPS = MutableStateFlow(false)

    val currentLocation: Flow<Pair<Double, Double>?>
    private val locationRequest = CurrentLocationRequest.Builder()
        .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
        .setMaxUpdateAgeMillis(30.minutes.inWholeMilliseconds)
        .build()

    init {
        currentLocation = canUseGPS
            .onEach { println("use gps? $it") }
            .map { shouldUseGps ->
                if (shouldUseGps)
                    locationClient.getCurrentLocation(
                        locationRequest,
                        CancellationTokenSource().token
                    ).await().let { it.latitude to it.longitude }
                else null
            }
    }

    fun setCanUseGPS(canUseGPS: Boolean) {
        this.canUseGPS.value = canUseGPS
    }

    companion object {
        @Volatile
        private var INSTANCE: LocationManager? = null

        fun getInstance(locationClient: FusedLocationProviderClient) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: LocationManager(locationClient).also { INSTANCE = it }
            }
    }
}