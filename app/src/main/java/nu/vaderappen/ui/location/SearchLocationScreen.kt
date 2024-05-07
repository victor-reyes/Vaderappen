package nu.vaderappen.ui.location

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel


const val ROUTE_SEARCH = "search"

@Composable
fun SearchLocationScreen(
    locationViewModel: LocationViewModel = viewModel(factory = LocationViewModel.Factory),
    onBackClicked: () -> Unit,
) {
    val locationUiState by locationViewModel.locationUiState.collectAsStateWithLifecycle()
    SearchLocationScreen(
        locationUiState = locationUiState,
        onBackClicked = onBackClicked,
        onSearch = locationViewModel::searchLocation,
        onLocationSelected = {
            locationViewModel.onLocationSelected(it)
            onBackClicked()
        },
        onFavedChange = locationViewModel::onFavedChange
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchLocationScreen(
    locationUiState: LocationUiState,
    onBackClicked: () -> Unit,
    onSearch: (String) -> Unit,
    onLocationSelected: (Location) -> Unit,
    onFavedChange: (location: Location, isFaved: Boolean) -> Unit,
) {
    Scaffold(
        topBar = {
            var query by remember { mutableStateOf("") }
            var active by remember { mutableStateOf(false) }
            DockedSearchBar(
                query = query,
                onQueryChange = { query = it },
                onSearch = { onSearch(query) },
                active = active,
                onActiveChange = { active = it },
            ) {
                when (locationUiState) {
                    LocationUiState.Loading -> CircularProgressIndicator()
                    is LocationUiState.Success -> LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize()
                    ) {
                        items(
                            locationUiState.searchedLocations,
                            key = { Triple(it.fullName, it.latitude, it.longitude) }
                        ) { location ->
                            Location(location,
                                onSelected = { onLocationSelected(location) },
                                onFavedChange = { onFavedChange(location, it) }
                            )
                        }
                    }
                }
            }
        },
    ) {
        Box(modifier = Modifier.padding(it)) {
            when (locationUiState) {
                LocationUiState.Loading -> {}
                is LocationUiState.Success -> Locations(
                    currentLocation = locationUiState.currentLocation,
                    favLocations = locationUiState.favedLocations,
                    onLocationSelected = onLocationSelected,
                    onFavedChange = onFavedChange
                )
            }
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Locations(
    currentLocation: Location?,
    favLocations: List<Location> = testLocations,
    onLocationSelected: (Location) -> Unit,
    onFavedChange: (location: Location, isFaved: Boolean) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        if (currentLocation != null) {
            stickyHeader("current") {
                StickyHeader("Nuvarande")
            }
            item(key = currentLocation.fullName + "_current"){
                Location(
                    location = currentLocation,
                    onSelected = { onLocationSelected(currentLocation) },
                    onFavedChange = { onFavedChange(currentLocation, it) },
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }
        stickyHeader("favs") {
            StickyHeader()
        }
        items(favLocations, key = { it.fullName}) { location ->
            Location(
                location = location,
                onSelected = { onLocationSelected(location) },
                onFavedChange = { onFavedChange(location, it) },
                modifier = Modifier.animateItemPlacement()
            )
        }
    }
}

@Composable
private fun StickyHeader(title: String = "Favoriter") {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Text(
            text = title, fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun Location(
    location: Location,
    modifier: Modifier = Modifier,
    onSelected: () -> Unit,
    onFavedChange: (Boolean) -> Unit,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .clickable(onClick = onSelected, role = Role.Button)
    ) {
        Row {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = location.name)
                Text(
                    text = location.fullName,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            var checked by remember { mutableStateOf(false) }
            IconToggleButton(checked = location.isFaved, onCheckedChange = onFavedChange) {
                Icon(
                    imageVector = if (location.isFaved) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Fav"
                )
            }
        }

        HorizontalDivider(thickness = Dp.Hairline)
    }
}


val testLocations = listOf(
    Location("New York", "New York", 40.7128, 74.0060),
    Location("Barcelona", "Barcelona", 41.3851, 2.1734),
    Location("London", "London", 51.5074, -0.1278),
    Location("Paris", "Paris", 48.8566, 2.3522),
    Location("Rome", "Rome", 41.9028, 12.4964),
    Location("Stockholm", "Stockholm", 59.3293, 18.0686),
)