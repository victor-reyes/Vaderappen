package nu.vaderappen.data.service.location.gps

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlin.time.Duration.Companion.minutes

@SuppressLint("MissingPermission")
class LocationManager(locationClient: FusedLocationProviderClient) {
    private val shouldUseGps = MutableStateFlow(false)

    val currentLocation: Flow<Location?>
    private val locationRequest = CurrentLocationRequest.Builder()
        .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
        .setMaxUpdateAgeMillis(30.minutes.inWholeMilliseconds)
        .build()

    init {
        currentLocation = shouldUseGps
            .map { shouldUseGps ->
                if (shouldUseGps)
                    locationClient.getCurrentLocation(
                        locationRequest,
                        CancellationTokenSource().token
                    ).result
                else null
            }
    }

    fun shouldUseGps(should: Boolean) {
        shouldUseGps.value = should
    }

}