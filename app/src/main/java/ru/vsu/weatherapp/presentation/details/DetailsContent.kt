package ru.vsu.weatherapp.presentation.details

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import ru.vsu.weatherapp.presentation.extensions.formattedFullDate
import ru.vsu.weatherapp.presentation.extensions.formattedShortDayOfWeek
import ru.vsu.weatherapp.presentation.extensions.tempToFormattedString
import ru.vsu.weatherapp.R
import ru.vsu.weatherapp.domain.entity.Forecast
import ru.vsu.weatherapp.domain.entity.Weather
import ru.vsu.weatherapp.presentation.ui.theme.Gradient.CardGradients.gradients

@Composable
fun DetailsContent(
    state: DetailsState,
    onBackClick: () -> Unit,
    onFavouriteClick: () -> Unit,
    onRetryClick: () -> Unit
) {
    val gradient = gradients[state.gradientIndex % gradients.size]
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradient.primaryGradient),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopBar(
                cityName = state.city.name,
                isCityFavourite = state.isFavourite,
                onBackClick = onBackClick,
                onRetryClick = onRetryClick,
                onClickChangeFavouriteStatus = onFavouriteClick
            )
        }
    ) { paddingValues ->
        Box(
            Modifier.padding(paddingValues)
        ) {
            when (val forecastState = state.forecastState) {
                DetailsState.ForecastState.Error -> {
                    Error(onRetryClick = onRetryClick)
                }

                DetailsState.ForecastState.Initial -> {
                    Initial()
                }

                is DetailsState.ForecastState.Loaded -> {
                    Forecast(forecast = forecastState.forecast)
                }

                DetailsState.ForecastState.Loading -> {
                    Loading()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    cityName: String,
    isCityFavourite: Boolean,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onClickChangeFavouriteStatus: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = cityName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.background
        ),
        navigationIcon = {
            IconButton(
                onClick = {
                    onBackClick()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.background
                )
            }
        },
        actions = {
            IconButton (
                onClick = onRetryClick,
            ) {
                Icon(
                    painter = painterResource(R.drawable.replay_48dp),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.background
                )
            }
            IconButton(
                onClick = {
                    onClickChangeFavouriteStatus()
                }
            ) {
                val icon = if (isCityFavourite) {
                    Icons.Default.Star
                } else {
                    Icons.Default.StarBorder
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.background
                )
            }
        }
    )
}

@Composable
private fun Error(onRetryClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Произошла ошибка!\nПроверьте подключение к интернету",
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.background
            )
            Image(
                painter = painterResource(R.drawable.android_wifi_alert_48dp),
                contentDescription = "Произошла ошибка!\nПроверьте подключение к интернету"
            )
            OutlinedButton (
                onClick = onRetryClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.background
                )
            ) {
                Text(text = "Повторить")
            }
        }
    }
}

@Composable
private fun Loading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.Center),
            color = MaterialTheme.colorScheme.background
        )
    }
}

@Composable
private fun Initial() {

}

@Composable
private fun Forecast(forecast: Forecast) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CurrentWeatherInfo(forecast.currentWeather)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AnimatedUpcomingWeather(
                    upcoming = forecast.upcoming
                )
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            CurrentWeatherInfo(forecast.currentWeather)

            Spacer(modifier = Modifier.weight(1f))

            AnimatedUpcomingWeather(
                upcoming = forecast.upcoming
            )

            Spacer(modifier = Modifier.weight(0.5f))
        }
    }
}

@Composable
private fun CurrentWeatherInfo(weather: Weather) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            style = MaterialTheme.typography.titleLarge,
            text = weather.conditionText
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 70.sp),
                text = weather.tempC.tempToFormattedString()
            )
            AsyncImage(
                modifier = Modifier.size(72.dp),
                model = weather.conditionUrl,
                contentDescription = null
            )
        }
        Text(
            style = MaterialTheme.typography.titleLarge,
            text = weather.date.formattedFullDate()
        )
    }
}

@Composable
private fun RowScope.SmallWeatherCard(weather: Weather) {
    Card(
        modifier = Modifier
            .height(128.dp)
            .weight(1f),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = weather.tempC.tempToFormattedString())
            AsyncImage(
                modifier = Modifier
                    .size(48.dp),
                model = weather.conditionUrl,
                contentDescription = null
            )
            Text(text = weather.date.formattedShortDayOfWeek())
        }
    }
}

@Composable
private fun AnimatedUpcomingWeather(upcoming: List<Weather>) {
    val state = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }
    AnimatedVisibility(
        visibleState = state,
        enter = fadeIn(animationSpec = tween(500)) + slideIn(
            animationSpec = tween(500),
            initialOffset = { IntOffset(0, it.height) }
        )
    ) {
        UpcomingWeather(upcoming)
    }
}

@Composable
fun UpcomingWeather(upcoming: List<Weather>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background.copy(
                alpha = 0.24f
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .align(Alignment.CenterHorizontally),
                text = "Ближайшее",
                style = MaterialTheme.typography.headlineMedium
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                upcoming.forEach {
                    SmallWeatherCard(
                        weather = it
                    )
                }
            }
        }
    }
}