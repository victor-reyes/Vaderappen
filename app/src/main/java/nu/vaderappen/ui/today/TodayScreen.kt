package nu.vaderappen.ui.today

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import nu.vaderappen.data.service.TimeSeries
import nu.vaderappen.data.service.Weather
import nu.vaderappen.data.service.precipitation
import nu.vaderappen.data.service.symbol
import nu.vaderappen.data.service.toSimpleDate
import nu.vaderappen.test.TestData
import nu.vaderappen.ui.forecast.ForecastUi
import nu.vaderappen.ui.forecast.ForecastViewModel
import nu.vaderappen.ui.forecast.Precipitation
import nu.vaderappen.ui.theme.VäderappenTheme
import kotlin.math.roundToInt


@Composable
fun TodayScreen(viewModel: ForecastViewModel = viewModel(factory = ForecastViewModel.Factory)) {
    val forecastUi by viewModel.forecastUi.collectAsStateWithLifecycle()
    TodayScreen(forecastUi = forecastUi)
}

@Composable
private fun TodayScreen(forecastUi: ForecastUi) {
    when (forecastUi) {
        is ForecastUi.Loading -> {}
        is ForecastUi.Forecast -> Today(forecastUi.weather)
    }
}

@Composable
private fun Today(weather: Weather) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val hours = weather.forecastByDay.flatMap { it.forecast }
        val listState = rememberLazyListState()
        val currentHour by remember {
            derivedStateOf {
                hours[listState.firstVisibleItemIndex + if (listState.firstVisibleItemScrollOffset > 0) 1 else 0]
            }
        }

        HourForecast(currentHour)
        LazyRow(
            state = listState,
        ) {
            items(hours.size, key = { hours[it].time }) { index ->
                val hour = hours[index]
                val colors = CardDefaults.elevatedCardColors().copy(
                    containerColor =
                    if (hour == currentHour) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.surface
                )
                ElevatedCard(
                    onClick = { scope.launch { listState.animateScrollToItem(index) } },
                    colors = colors,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    CompactForecast(hour)
                }

            }
        }
    }
}

@Composable
private fun CompactForecast(hour: TimeSeries) {
    val date = hour.time.toLocalDate().toSimpleDate()
    val time = hour.time.toLocalTime().toString()
    Column(modifier = Modifier.padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = date,
            fontWeight = FontWeight.Bold
        )
        Text(text = time, modifier = Modifier.fillMaxWidth())
        Row(modifier = Modifier.height(IntrinsicSize.Max)) {
            hour.symbol?.drawableId?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
            }
            with(hour.data.instant.details) {
                Temperature(airTemperature.roundToInt())
            }
        }
        hour.precipitation?.let {
            Precipitation(
                precipitation = it,
                detailed = false
            )
        } ?: Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun HourForecast(hour: TimeSeries) {
    val date = hour.time.toLocalDate().toSimpleDate()
    val time = hour.time.toLocalTime().toString()
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = date,
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontWeight = FontWeight.Bold
        )
        Text(text = time)
        Row(modifier = Modifier.height(IntrinsicSize.Max)) {
            hour.symbol?.drawableId?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = null
                )
            }
            with(hour.data.instant.details) {
                Temperature(
                    airTemperature.roundToInt(),
                    airTemperaturePercentile10?.roundToInt(),
                    airTemperaturePercentile90?.roundToInt(),
                )
            }
        }
        hour.precipitation?.let { Precipitation(it) } ?: Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun Temperature(
    airTemperature: Int,
    airTemperaturePercentile10: Int? = null,
    airTemperaturePercentile90: Int? = null,
) {

    val color = if (airTemperature > 0) Color.Red else Color.Blue
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(IntrinsicSize.Max)
            .wrapContentSize()
    ) {
        Text(
            text = "$airTemperature°",
            color = color,
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize()
        )
        if (airTemperaturePercentile10 != null && airTemperaturePercentile90 != null) {
            val colorMin =
                if (airTemperaturePercentile10 > 0) Color.Red else Color.Blue
            val colorMax =
                if (airTemperaturePercentile90 > 0) Color.Red else Color.Blue
            val minMaxTemp = buildAnnotatedString {
                append("(")
                withStyle(style = SpanStyle(color = colorMin)) {
                    append("$airTemperaturePercentile10°")
                }
                append(" - ")
                withStyle(style = SpanStyle(color = colorMax)) {
                    append("$airTemperaturePercentile90°")
                }
                append(")")
            }
            Text(
                text = minMaxTemp,
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview
@Composable
private fun TodayScreenPreview() {
    VäderappenTheme {
        Surface {
            Today(TestData.weather)
        }
    }
}