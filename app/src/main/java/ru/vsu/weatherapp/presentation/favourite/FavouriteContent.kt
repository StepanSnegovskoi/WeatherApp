package ru.vsu.weatherapp.presentation.favourite

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import ru.vsu.weatherapp.R
import ru.vsu.weatherapp.domain.entity.City
import ru.vsu.weatherapp.presentation.extensions.tempToFormattedString
import ru.vsu.weatherapp.presentation.ui.theme.Gradient
import ru.vsu.weatherapp.presentation.ui.theme.Orange

@Composable
fun FavouriteContent(
    state: FavouriteUiState,
    onCityItemClick: (City, Int) -> Unit,
    onSearchClick: () -> Unit,
    onAddFavouriteClick: () -> Unit,
    onRetryClick: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            columns = GridCells.Fixed(2),
        ) {
            item(
                span = { GridItemSpan(2) }
            ) {
                Row(
                    modifier = Modifier.fillMaxSize()
                ) {
                    SearchCard(
                        modifier = Modifier
                            .weight(1f),
                        onClick = onSearchClick
                    )
                    IconButton (
                        onClick = onRetryClick,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.replay_48dp),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
            itemsIndexed(
                items = state.cityItems,
                key = { _, item -> item.city.id }
            ) { index, cityItem ->
                CityItem(
                    index = index,
                    cityItem = cityItem,
                    onClick = {
                        onCityItemClick(cityItem.city, index)
                    }
                )
            }

            item {
                AddFavouriteCityCard(
                    onClick = onAddFavouriteClick
                )
            }
        }
    }
}

@Composable
private fun CityItem(
    cityItem: FavouriteUiState.CityItem,
    index: Int,
    onClick: () -> Unit
) {
    val gradient = getGradientByIndex(index)
    Card(
        modifier = Modifier
            .fillMaxSize()
            .shadow(
                elevation = 16.dp,
                spotColor = gradient.shadowColor,
                shape = MaterialTheme.shapes.extraLarge
            )
            .clickable {
                onClick()
            },
        colors = CardDefaults.cardColors(containerColor = Color.Blue),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Box(
            modifier = Modifier
                .background(gradient.primaryGradient)
                .fillMaxSize()
                .sizeIn(minHeight = 196.dp)
                .padding(24.dp)
                .drawBehind {
                    drawCircle(
                        brush = gradient.secondaryGradient,
                        center = Offset(
                            x = size.width / 2,
                            y = size.height + size.height / 4
                        ),
                        radius = size.maxDimension / 1.5f
                    )
                }

        ) {

            when (val weatherState = cityItem.weatherState) {
                FavouriteUiState.WeatherState.Error -> {
                    Image(
                        painter = painterResource(R.drawable.android_wifi_alert_48dp),
                        contentDescription = "Произошла ошибка!\nПроверьте подключение к интернету"
                    )
                }

                FavouriteUiState.WeatherState.Initial -> {
                }

                is FavouriteUiState.WeatherState.Loaded -> {
                    AsyncImage(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(56.dp),
                        model = weatherState.iconUrl,
                        contentDescription = null
                    )
                    Text(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(bottom = 24.dp),
                        text = weatherState.tempC.tempToFormattedString(),
                        color = MaterialTheme.colorScheme.background,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 48.sp)
                    )
                }

                FavouriteUiState.WeatherState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center),
                        color = MaterialTheme.colorScheme.background
                    )
                }
            }

            Text(
                modifier = Modifier.align(Alignment.BottomStart),
                text = cityItem.city.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.background
            )
        }
    }
}

private fun getGradientByIndex(index: Int): Gradient {
    val gradients = Gradient.CardGradients.gradients
    return gradients[index % gradients.size]
}

@Composable
private fun SearchCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val gradient = Gradient.CardGradients.gradients[3]
    Card(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )  {
                onClick()
            },
        shape = CircleShape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient.primaryGradient),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                imageVector = Icons.Default.Search,
                tint = MaterialTheme.colorScheme.background,
                contentDescription = null,
            )
            Text(
                modifier = Modifier
                    .padding(16.dp),
                text = stringResource(R.string.search),
                color = MaterialTheme.colorScheme.background
            )
        }
    }
}

@Composable
private fun AddFavouriteCityCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = MaterialTheme.shapes.extraLarge,
        border = BorderStroke(width = 1.dp, MaterialTheme.colorScheme.onBackground)
    ) {
        Column(
            modifier = Modifier
                .sizeIn(minHeight = 196.dp)
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
                    .size(48.dp),
                imageVector = Icons.Default.Edit,
                tint = Orange,
                contentDescription = null
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.titleMedium,
                text = stringResource(R.string.button_add_favourite)
            )
        }
    }
}