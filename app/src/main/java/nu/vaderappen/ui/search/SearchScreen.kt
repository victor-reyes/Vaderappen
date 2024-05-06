package nu.vaderappen.ui.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


const val ROUTE_SEARCH = "search"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(
    locations: List<Location> = testLocations,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        stickyHeader("favs") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Favoriter", fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        items(locations, key = { Triple(it.name, it.latitude, it.longitude) }) { location ->
            Column(Modifier.padding(16.dp)) {
                Row {
                    Text(text = location.name)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${location.latitude}: ${location.longitude}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                HorizontalDivider(thickness = Dp.Hairline)
            }
        }

    }
}

data class Location(val name: String, val latitude: Double, val longitude: Double)

val testLocations = listOf(
    Location("New York", 40.7128, 74.0060),
    Location("Barcelona", 41.3851, 2.1734),
    Location("London", 51.5074, -0.1278),
    Location("Paris", 48.8566, 2.3522),
    Location("Rome", 41.9028, 12.4964),
    Location("Stockholm", 59.3293, 18.0686),
)