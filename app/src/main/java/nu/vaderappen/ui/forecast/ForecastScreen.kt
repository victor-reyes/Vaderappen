package nu.vaderappen.ui.forecast

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Send
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import nu.vaderappen.data.service.Day
import nu.vaderappen.data.service.Precipitation
import nu.vaderappen.data.service.precipitation
import nu.vaderappen.data.service.symbol
import nu.vaderappen.test.TestData
import nu.vaderappen.ui.theme.VäderappenTheme
import java.util.Locale


@Composable
fun ForecastScreen(viewModel: ForecastViewModel = viewModel(factory = ForecastViewModel.Factory)) {
    val forecastUi by viewModel.forecastUi.collectAsStateWithLifecycle()
    ForecastScreen(forecastUi)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ForecastScreen(forecastUi: ForecastUi) {
    when (forecastUi) {
        is ForecastUi.Forecast -> {
            LazyColumn(
                contentPadding = PaddingValues(8.dp),
            ) {
                forecastUi.weather.forecastByDay.map { day ->
                    stickyHeader {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = day.date,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp),
                            )
                        }
                    }
                    item { Day(day) }
                }
            }

        }

        ForecastUi.Loading -> {}
    }
}

@Composable
private fun Day(day: Day) {
    ElevatedCard {
        Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)) {
            day.forecast.map { hour ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = String.format(Locale.getDefault(), "%02d", hour.time.hour),
                        modifier = Modifier
                            .size(24.dp)
                            .wrapContentSize()
                    )
                    hour.symbol?.let {
                        Image(
                            painter = painterResource(id = it.drawableId),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.size(32.dp)
                        )
                    } ?: Spacer(modifier = Modifier.size(32.dp))
                    val temp = hour.data.instant.details.airTemperature
                    Text(
                        text = "${temp.toInt()}°",
                        color = if (temp > 0) Color.Red else Color.Blue,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .height(32.dp)
                            .width(34.dp)
                            .wrapContentSize(Alignment.CenterEnd)
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    hour.precipitation?.let {
                        Precipitation(
                            precipitation = it,
                            MaterialTheme.typography.bodySmall.fontSize
                        )
                    }
                    with(hour.data.instant.details) {
                        Text(
                            "$windSpeed${windSpeedOfGust?.let { " (${it})" } ?: ""}",
                            modifier = Modifier
                                .height(32.dp)
                                .wrapContentSize()
                        )
                        Image(
                            imageVector = Icons.Sharp.Send,
                            contentDescription = null,
                            contentScale = ContentScale.FillHeight,
                            modifier = Modifier
                                .size(16.dp)
                                .wrapContentSize()
                                .rotate(windFromDirection.toFloat() + 90),
                            colorFilter = ColorFilter.tint(Color.Green)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Precipitation(
    precipitation: Precipitation,
    fontSize: TextUnit = MaterialTheme.typography.titleMedium.fontSize,
) {
    Text(
        text = precipitation.toString(),
        color = Color.Blue.copy(
            precipitation.probability?.let { (it * 1.5f + 33).coerceAtMost(100f) / 100 } ?: 1f),
        fontSize = fontSize,
        fontWeight = FontWeight.Bold,
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ForecestScreenPreview() {
    val uiState = ForecastUi.Forecast(weather = TestData.weather)
    VäderappenTheme {
        Surface {
            ForecastScreen(forecastUi = uiState)
        }
    }

}