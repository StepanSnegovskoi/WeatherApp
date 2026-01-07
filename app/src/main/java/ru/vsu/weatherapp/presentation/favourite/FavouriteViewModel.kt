package ru.vsu.weatherapp.presentation.favourite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.vsu.weatherapp.domain.entity.City
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.vsu.weatherapp.domain.usecase.remote.GetCurrentWeatherUseCase
import ru.vsu.weatherapp.domain.usecase.local.GetFavouriteCitiesUseCase
import javax.inject.Inject

data class FavouriteUiState(
    val cityItems: List<CityItem> = emptyList()
) {
    data class CityItem(
        val city: City,
        val weatherState: WeatherState = WeatherState.Initial
    )

    sealed interface WeatherState {
        data object Initial : WeatherState
        data object Loading : WeatherState
        data object Error : WeatherState
        data class Loaded(
            val tempC: Float,
            val iconUrl: String
        ) : WeatherState
    }
}

sealed interface FavouriteSideEffect {
    data object NavigateToSearch : FavouriteSideEffect
    data object NavigateToAddToFavourite : FavouriteSideEffect
    data class NavigateToDetails(val city: City, val index: Int) : FavouriteSideEffect
}

@HiltViewModel
class FavouriteViewModel @Inject constructor(
    private val getFavouriteCitiesUseCase: GetFavouriteCitiesUseCase,
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FavouriteUiState())
    val state: StateFlow<FavouriteUiState> = _state.asStateFlow()

    private val _sideEffects = Channel<FavouriteSideEffect>()
    val sideEffects = _sideEffects.receiveAsFlow()

    init {
        observeFavouriteCities()
    }

    private fun observeFavouriteCities() {
        getFavouriteCitiesUseCase()
            .onEach { cities ->
                val currentItems = _state.value.cityItems.associateBy { it.city.id }

                val newItems = cities.map { city ->
                    currentItems[city.id]?.copy(city = city)
                        ?: FavouriteUiState.CityItem(city = city, weatherState = FavouriteUiState.WeatherState.Initial)
                }

                _state.update { it.copy(cityItems = newItems) }

                newItems.forEach { item ->
                    loadWeather(item.city)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadWeather(city: City) {
        viewModelScope.launch {
            updateCityWeatherState(city.id, FavouriteUiState.WeatherState.Loading)
            try {
                val weather = getCurrentWeatherUseCase(city.id)
                updateCityWeatherState(
                    city.id,
                    FavouriteUiState.WeatherState.Loaded(
                        tempC = weather.tempC,
                        iconUrl = weather.conditionUrl
                    )
                )
            } catch (_: Exception) {
                updateCityWeatherState(city.id, FavouriteUiState.WeatherState.Error)
            }
        }
    }

    private fun updateCityWeatherState(cityId: Int, newWeatherState: FavouriteUiState.WeatherState) {
        _state.update { currentState ->
            val newItems = currentState.cityItems.map { item ->
                if (item.city.id == cityId) {
                    item.copy(weatherState = newWeatherState)
                } else {
                    item
                }
            }
            currentState.copy(cityItems = newItems)
        }
    }

    fun onSearchClick() {
        viewModelScope.launch { _sideEffects.send(FavouriteSideEffect.NavigateToSearch) }
    }

    fun onAddFavouriteClick() {
        viewModelScope.launch { _sideEffects.send(FavouriteSideEffect.NavigateToAddToFavourite) }
    }

    fun onCityItemClick(city: City, index: Int) {
        viewModelScope.launch { _sideEffects.send(FavouriteSideEffect.NavigateToDetails(city, index)) }
    }

    fun onRetryClick() {
        viewModelScope.launch {
            _state.value.cityItems.forEach { cityItem ->
                loadWeather(cityItem.city)
            }
        }
    }
}