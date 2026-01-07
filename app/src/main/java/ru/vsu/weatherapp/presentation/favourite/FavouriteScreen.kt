package ru.vsu.weatherapp.presentation.favourite

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.vsu.weatherapp.domain.entity.City

@Composable
fun FavouriteScreen(
    onCityItemClicked: (City, Int) -> Unit,
    onSearchClicked: () -> Unit,
    onAddToFavouriteClicked: () -> Unit,
    viewModel: FavouriteViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                FavouriteSideEffect.NavigateToAddToFavourite -> onAddToFavouriteClicked()
                FavouriteSideEffect.NavigateToSearch -> onSearchClicked()
                is FavouriteSideEffect.NavigateToDetails -> onCityItemClicked(effect.city, effect.index)
            }
        }
    }

    FavouriteContent(
        state = state,
        onCityItemClick = viewModel::onCityItemClick,
        onSearchClick = viewModel::onSearchClick,
        onAddFavouriteClick = viewModel::onAddFavouriteClick,
        onRetryClick = viewModel::onRetryClick
    )
}