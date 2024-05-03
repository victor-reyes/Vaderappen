package nu.vaderappen.ui.forecast

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Send
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import nu.vaderappen.data.service.getPrecipitationAmount
import nu.vaderappen.data.service.getSymbol
import java.util.Locale


@Composable
fun ForecastScreen(viewModel: ForecastViewModel = viewModel(factory = ForecastViewModel.Factory)) {
    val forecastUi by viewModel.forecastUi.collectAsStateWithLifecycle()
    ForecastScreen(forecastUi)
}

@Composable
private fun ForecastScreen(forecastUi: ForecastUi) {
    when (forecastUi) {
        is ForecastUi.Forecast -> {
            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(forecastUi.weather.forecastByDay) { day ->
                    ElevatedCard {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(day.date, style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(16.dp))
                            day.forecast.map { hour ->
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                                        Text(
                                            text = String
                                                .format(Locale.getDefault(), "%02d", hour.time.hour)
                                        )
                                        hour.getSymbol()?.let {
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
                                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                                        )
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        with(hour.data.instant.details) {
                                            Text(
                                                text = hour.getPrecipitationAmount().toString(),
                                                color = if (hour.getPrecipitationAmount() > 0) Color.Blue else Color.LightGray
                                            )
                                            Text("$windSpeed ${day.units.windSpeed}")
                                            Icon(
                                                imageVector = Icons.Sharp.Send,
                                                contentDescription = null,
                                                tint = Color.Blue.copy(alpha = .53f),
                                                modifier = Modifier.rotate(windFromDirection.toFloat() + 90)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }

        ForecastUi.Loading -> {}
    }
}