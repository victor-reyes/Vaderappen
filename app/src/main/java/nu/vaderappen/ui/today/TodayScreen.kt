package nu.vaderappen.ui.today

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Send
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import nu.vaderappen.data.service.weather.TimeSeries
import nu.vaderappen.data.service.weather.Weather
import nu.vaderappen.data.service.weather.precipitation
import nu.vaderappen.data.service.weather.symbol
import nu.vaderappen.data.service.weather.toSimpleDate
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
        is ForecastUi.Success -> Today(forecastUi.weather)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Today(
    weather: Weather,
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,
) {
    val isLargeScreen = windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT

    println("Large Screen: $isLargeScreen ${windowSizeClass.windowWidthSizeClass}")

    val hours = weather.forecastByDay.flatMap { it.forecast }
    val pagerState = rememberPagerState(pageCount = { hours.size })
    LaunchedEffect(key1 = weather) {
        pagerState.scrollToPage(0)
    }
    val currentHourIndex by remember { derivedStateOf { pagerState.currentPage } }
    val currentHour = hours[currentHourIndex]

    if (isLargeScreen) {
        Row {
            PerHourPager(pagerState, hours, currentHour, true)
            Spacer(modifier = Modifier.weight(1f))
            HourForecast(currentHour)
        }
    } else
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HourForecast(currentHour)
            Spacer(modifier = Modifier.weight(1f))
            PerHourPager(pagerState, hours, currentHour)
        }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun PerHourPager(
    pagerState: PagerState,
    hours: List<TimeSeries>,
    currentHour: TimeSeries,
    isLargeScreen: Boolean = false,
) {
    if (isLargeScreen) {
        Row {
            VerticalPager(
                state = pagerState,
                pageSize = PageSize.Fixed(140.dp),
                contentPadding = PaddingValues(vertical = 120.dp),
                pageSpacing = 8.dp,
            ) {
                val hour = hours[it]
                val colors = CardDefaults.elevatedCardColors()
                    .copy(
                        containerColor = if (currentHour == hour) MaterialTheme.colorScheme.surfaceContainerHighest
                        else MaterialTheme.colorScheme.surfaceContainerLowest
                    )
                ElevatedCard(colors = colors, modifier = Modifier.width(140.dp)) {
                    CompactForecast(hour)
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(4.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }


    } else
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    "Timme för timme: ",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.contentColorFor(MaterialTheme.colorScheme.primary)
                )
            }
            HorizontalPager(
                state = pagerState,
                pageSize = PageSize.Fixed(140.dp),
                contentPadding = PaddingValues(horizontal = 120.dp),
                pageSpacing = 8.dp,
            ) {
                val hour = hours[it]
                val colors = CardDefaults.elevatedCardColors()
                    .copy(
                        containerColor = if (currentHour == hour) MaterialTheme.colorScheme.surfaceContainerHighest
                        else MaterialTheme.colorScheme.surfaceContainerLowest
                    )
                ElevatedCard(colors = colors, modifier = Modifier.height(140.dp)) {
                    CompactForecast(hour)
                }
            }
        }
}

@Composable
private fun CompactForecast(hour: TimeSeries) {
    val date = hour.time.toLocalDate().toSimpleDate()
    val time = hour.time.toLocalTime().toString()
    Column(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = date,
            fontWeight = FontWeight.Bold
        )
        Text(text = time)
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
    OutlinedCard(
        modifier = Modifier
            .wrapContentSize()
            .verticalScroll(rememberScrollState())
            .padding(8.dp),
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = date,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.Bold
            )
            Text(text = time)
            Column {
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
                        Spacer(modifier = Modifier.weight(1f))
                        Wind(
                            windSpeed.roundToInt(),
                            windFromDirection.roundToInt(),
                            windSpeedOfGust?.roundToInt()
                        )
                    }
                }
                hour.precipitation?.let { Precipitation(it) }
                    ?: Spacer(modifier = Modifier.height(32.dp))
            }

            with(hour.data.instant.details) {
                Row {
                    ultravioletIndexClearSky?.let { UVIndexScale(uvIndex = it.toFloat()) }
                        ?: Spacer(modifier = Modifier.width(120.dp))
                    Spacer(modifier = Modifier.weight(1f))
                    Column {
                        val fontWeight = FontWeight.Bold
                        val fontSize = MaterialTheme.typography.bodySmall.fontSize
                        Text(
                            text = "Luftfuktighet: $relativeHumidity%",
                            fontWeight = fontWeight, fontSize = fontSize
                        )
                        Text(
                            text = "Lufttryck: $airPressureAtSeaLevel hPa",
                            fontWeight = fontWeight, fontSize = fontSize
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UVIndexScale(uvIndex: Float, maxUVIndex: Float = 11f) {
    val color = when (uvIndex) {
        in 0f..0.99f -> Color.LightGray
        in 1f..2.5f -> Color(0xFF76FF03)
        in 2.5f..5.5f -> Color(0xFFFFEB3B)
        in 5.5f..7.5f -> Color(0xFFFF9800)
        in 7.5f..10f -> Color(0xFFF44336)
        else -> Color(0xFFD50000)
    }

    Surface(color = color, shape = CircleShape) {
        Text(
            text = "UV: $uvIndex",
            color = MaterialTheme.colorScheme.contentColorFor(color),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun Wind(windSpeed: Int, windFromDirection: Int, windSpeedOfGust: Int? = null) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val windAnnotatedString = buildAnnotatedString {
            append("Vind: ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("$windSpeed")
            }
            windSpeedOfGust?.let { append(" ($it)") }
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(" m/s")
            }
        }
        Text(windAnnotatedString)
        Row {
            repeat(3) {
                Image(
                    imageVector = Icons.Sharp.Send,
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .size(32.dp)
                        .rotate(windFromDirection.toFloat() + 90),
                    colorFilter = ColorFilter.tint(Color.Green)
                )
            }
        }
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