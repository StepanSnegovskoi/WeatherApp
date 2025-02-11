package com.example.weatherapp.presentation.details

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideIn
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
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.weatherapp.domain.entity.Forecast
import com.example.weatherapp.domain.entity.Weather
import com.example.weatherapp.presentation.extensions.formattedFullDate
import com.example.weatherapp.presentation.extensions.formattedShortDayOfWeek
import com.example.weatherapp.presentation.extensions.tempToFormattedString
import com.example.weatherapp.presentation.ui.theme.Gradient

/**
 * 11.36 -21:01
 */
@Composable
fun DetailsContent(component: DetailsComponent) {
    val state by component.model.collectAsState()
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Gradient.CardGradients.gradients[1].primaryGradient),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopBar(
                cityName = state.city.name,
                isCityFavourite = state.isFavourite,
                onBackClick = {
                    component.onClickBack()
                },
                onClickChangeFavouriteStatus = {
                    component.onClickFavouriteStatus()
                }
            )
        }
    ) { paddingValues ->
        Box(
            Modifier.padding(paddingValues)
        ) {
            when (val forecastState = state.forecastState) {
                DetailsStore.State.ForecastState.Error -> {
                    Error()
                    Log.d("test", "Error")
                }

                DetailsStore.State.ForecastState.Initial -> {
                    Initial()
                    Log.d("test", "Initial")
                }

                is DetailsStore.State.ForecastState.Loaded -> {
                    Forecast(forecast = forecastState.forecast)
                    Log.d("test", "Forecast")
                }

                DetailsStore.State.ForecastState.Loading -> {
                    Loading()
                    Log.d("test", "Loading")
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
    onClickChangeFavouriteStatus: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = cityName)
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
private fun Error() {

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

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun Forecast(forecast: Forecast) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(
            style = MaterialTheme.typography.titleLarge,
            text = forecast.currentWeather.conditionText
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 70.sp),
                text = forecast.currentWeather.tempC.tempToFormattedString()
            )
            GlideImage(
                modifier = Modifier
                    .size(72.dp),
                model = forecast.currentWeather.conditionUrl,
                contentDescription = null
            )
        }
        Text(
            style = MaterialTheme.typography.titleLarge,
            text = forecast.currentWeather.date.formattedFullDate()
        )
        Spacer(modifier = Modifier.weight(1f))
        AnimatedUpcomingWeather(
            upcoming = forecast.upcoming
        )
        Spacer(modifier = Modifier.weight(0.5f))
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
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
            GlideImage(
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
private fun AnimatedUpcomingWeather(upcoming: List<Weather>){
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