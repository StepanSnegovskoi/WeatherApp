package ru.vsu.weatherapp.presentation.navigation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class NavigationState(
    val backStack: List<WeatherRoute> = listOf(WeatherRoute.Favourite)
)

@HiltViewModel
class NavigationViewModel @Inject constructor() : ViewModel() {

    private val _navigationState = MutableStateFlow(NavigationState())
    val navigationState: StateFlow<NavigationState> = _navigationState.asStateFlow()

    fun navigateTo(route: WeatherRoute) {
        _navigationState.update { state ->
            state.copy(backStack = state.backStack + route)
        }
    }

    fun navigateBack() {
        _navigationState.update { state ->
            if (state.backStack.size > 1) {
                state.copy(backStack = state.backStack.dropLast(1))
            } else {
                state
            }
        }
    }
}