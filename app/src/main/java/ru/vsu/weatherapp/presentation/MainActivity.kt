package ru.vsu.weatherapp.presentation

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.vsu.weatherapp.presentation.details.DetailsViewModel
import ru.vsu.weatherapp.presentation.navigation.WeatherApp
import ru.vsu.weatherapp.presentation.search.SearchViewModel
import ru.vsu.weatherapp.presentation.ui.theme.WeatherAppTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var detailsViewModelFactory: DetailsViewModel.Factory

    @Inject
    lateinit var searchViewModelFactory: SearchViewModel.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var keepSplashOnScreen = true

        splashScreen.setKeepOnScreenCondition {
            keepSplashOnScreen
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        lifecycleScope.launch {
            delay(1000)
            keepSplashOnScreen = false
        }

        setContent {
            WeatherAppTheme {
                WeatherApp(
                    detailsViewModelFactory = detailsViewModelFactory,
                    searchViewModelFactory = searchViewModelFactory
                )
            }
        }
    }
}