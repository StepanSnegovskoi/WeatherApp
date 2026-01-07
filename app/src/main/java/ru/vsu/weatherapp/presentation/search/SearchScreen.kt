package ru.vsu.weatherapp.presentation.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.vsu.weatherapp.domain.entity.City

@Composable
fun SearchScreen(
    onBackClicked: () -> Unit,
    onCitySavedToFavourite: () -> Unit,
    onForecastForCityRequested: (City) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                SearchSideEffect.NavigateBack -> {
                    onBackClicked()
                }
                is SearchSideEffect.OpenForecast -> {
                    onForecastForCityRequested(effect.city)
                }
                SearchSideEffect.SavedToFavourite -> {
                    onCitySavedToFavourite()
                }
            }
        }
    }

    SearchContent(
        state = state,
        onSearchQueryChanged = viewModel::changeSearchQuery,
        onSearchClick = viewModel::onSearchClick,
        onCityClick = viewModel::onCityClick,
        onBackClick = viewModel::onBackClick
    )
}