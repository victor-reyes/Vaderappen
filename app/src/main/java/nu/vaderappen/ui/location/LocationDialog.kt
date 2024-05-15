package nu.vaderappen.ui.location

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.isGranted

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationDialog(
    locationPermissionsState: MultiplePermissionsState,
    onLocationPermissionChange: (shouldUSeGPS: Boolean) -> Unit) {

    LaunchedEffect(locationPermissionsState.permissions
        .any { it.status.isGranted }) {
        locationPermissionsState.launchMultiplePermissionRequest()
        onLocationPermissionChange(
            locationPermissionsState.permissions
                .any { it.status.isGranted })
    }
}