package nu.vaderappen.ui.location

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationDialog() {
    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
        )
    )
    println(locationPermissionsState.toString())
    LaunchedEffect(key1 = locationPermissionsState) {
        println(locationPermissionsState)
        if (!locationPermissionsState.allPermissionsGranted) {
            println("here")
            locationPermissionsState.launchMultiplePermissionRequest()
        }
    }
}