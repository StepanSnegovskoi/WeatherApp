package ru.vsu.weatherapp.presentation.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun DetailsScreen(
    viewModel: DetailsViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                DetailsSideEffect.NavigateBack -> onBackClick()
            }
        }
    }

    DetailsContent(
        state = state,
        onBackClick = viewModel::onBackClick,
        onFavouriteClick = viewModel::onFavouriteClick,
        onRetryClick = viewModel::onRetryClick
    )
}