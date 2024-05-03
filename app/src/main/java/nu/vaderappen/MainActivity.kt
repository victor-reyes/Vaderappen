package nu.vaderappen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import nu.vaderappen.ui.forecast.ForecastScreen
import nu.vaderappen.ui.theme.VäderappenTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VäderappenTheme {
                Scaffold {
                    Box(modifier = Modifier.padding(it)) {
                        ForecastScreen()
                    }
                }
            }
        }
    }
}

