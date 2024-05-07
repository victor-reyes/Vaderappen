package nu.vaderappen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.map
import nu.vaderappen.data.service.prefs.PrefsRepository
import nu.vaderappen.data.service.prefs.dataStore
import nu.vaderappen.ui.forecast.ForecastScreen
import nu.vaderappen.ui.location.ROUTE_SEARCH
import nu.vaderappen.ui.location.SearchLocationScreen
import nu.vaderappen.ui.theme.VäderappenTheme
import nu.vaderappen.ui.today.TodayScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val prefsRepository = PrefsRepository(LocalContext.current.dataStore)
            val location by prefsRepository.location.map { it.name }
                .collectAsStateWithLifecycle(initialValue = "")
            VäderappenTheme {
                val navController = rememberNavController()
                val current by navController.currentBackStackEntryAsState()
                Scaffold(
                    topBar = { WeatherTopBar(location, current, navController) },
                    bottomBar = { WeatherNavBar(current, navController) }
                ) {
                    Box(modifier = Modifier.padding(it)) {
                        WeatherNavHost(navHostController = navController)
                    }
                }
            }
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun WeatherTopBar(
        currentLocation: String,
        current: NavBackStackEntry?,
        navController: NavHostController,
    ) {
        if (current?.destination?.route != ROUTE_SEARCH)
            TopAppBar(
                title = { Text(text = currentLocation) },
                modifier = Modifier.shadow(8.dp),
                actions = {
                    IconButton(onClick = { navController.navigate(ROUTE_SEARCH) }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Sök"
                        )
                    }

                }
            )
    }
}

@Composable
private fun WeatherNavBar(current: NavBackStackEntry?, navController: NavHostController) {
    NavigationBar {
        val barItems = NavBarDestination.items
        barItems.map { navBarItem ->
            val selected = current?.destination?.hierarchy?.any {
                it.route?.contains(navBarItem.route) ?: false
            } ?: false
            val animatedColor by animateColorAsState(
                targetValue = if (selected) Color.Blue else Color.DarkGray,
                animationSpec = tween(800)
            )
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(navBarItem.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = navBarItem.resId),
                        contentDescription = navBarItem.title,
                        tint = animatedColor
                    )
                },
                label = { Text(text = navBarItem.title, color = animatedColor) },
                colors = NavigationBarItemDefaults.colors()
                    .copy(selectedIndicatorColor = Color.Transparent)
            )
        }
    }
}


@Composable
private fun WeatherNavHost(navHostController: NavHostController) {
    val startDestination = NavBarDestination.ROUTE_TODAY
    NavHost(navController = navHostController, startDestination = startDestination) {
        NavBarDestination.items.map { destination ->
            when (destination) {
                NavBarDestination.Today -> composable(destination.route) { TodayScreen() }
                NavBarDestination.Forecast -> composable(destination.route) { ForecastScreen() }
                NavBarDestination.Settings -> composable(destination.route) { Text(text = "Inställnigar") }
            }
        }
        composable(route = ROUTE_SEARCH) {
            SearchLocationScreen(onBackClicked = navHostController::popBackStack)
        }
    }
}

sealed class NavBarDestination(val route: String, val title: String, @DrawableRes val resId: Int) {
    data object Today : NavBarDestination(
        ROUTE_TODAY,
        "Idag",
        R.drawable.baseline_sunny_24
    )

    data object Forecast : NavBarDestination(
        ROUTE_FORECAST,
        "10-dygn",
        R.drawable.baseline_list_24
    )

    data object Settings : NavBarDestination(
        ROUTE_SETTINGS,
        "Inställnigar",
        R.drawable.baseline_settings_24
    )

    companion object {
        val items = listOf(Today, Forecast, Settings)

        const val ROUTE_TODAY = "today"
        const val ROUTE_FORECAST = "forecast"
        const val ROUTE_SETTINGS = "prefs"
    }
}

