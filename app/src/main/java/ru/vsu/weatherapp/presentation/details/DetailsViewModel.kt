package ru.vsu.weatherapp.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.vsu.weatherapp.domain.entity.City
import ru.vsu.weatherapp.domain.usecase.local.AddCityToFavouriteUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.vsu.weatherapp.domain.entity.Forecast
import ru.vsu.weatherapp.domain.usecase.remote.GetForecastUseCase
import ru.vsu.weatherapp.domain.usecase.local.ObserveFavouriteStateUseCase
import ru.vsu.weatherapp.domain.usecase.local.RemoveCityFromFavouriteUseCase

data class DetailsState(
    val city: City,
    val isFavourite: Boolean = false,
    val forecastState: ForecastState = ForecastState.Initial,
    val gradientIndex: Int = 1
) {
    sealed interface ForecastState {
        data object Initial : ForecastState
        data object Loading : ForecastState
        data class Loaded(val forecast: Forecast) : ForecastState
        data object Error : ForecastState
    }
}

sealed interface DetailsSideEffect {
    data object NavigateBack : DetailsSideEffect
}

class DetailsViewModel @AssistedInject constructor(
    @Assisted val city: City,
    @Assisted val index: Int,
    private val getForecastUseCase: GetForecastUseCase,
    private val addCityToFavouriteUseCase: AddCityToFavouriteUseCase,
    private val removeCityFromFavouriteUseCase: RemoveCityFromFavouriteUseCase,
    private val observeFavouriteStateUseCase: ObserveFavouriteStateUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DetailsState(city = city, gradientIndex = index))
    val state: StateFlow<DetailsState> = _state.asStateFlow()

    private val _sideEffects = Channel<DetailsSideEffect>()
    val sideEffects = _sideEffects.receiveAsFlow()

    init {
        observeFavouriteStatus()
        loadForecast()
    }

    fun onBackClick() {
        viewModelScope.launch {
            _sideEffects.send(DetailsSideEffect.NavigateBack)
        }
    }

    fun onFavouriteClick() {
        viewModelScope.launch {
            val currentState = _state.value
            if (currentState.isFavourite) {
                removeCityFromFavouriteUseCase(city.id)
            } else {
                addCityToFavouriteUseCase(city)
            }
        }
    }

    fun onRetryClick() {
        loadForecast()
    }

    private fun observeFavouriteStatus() {
        observeFavouriteStateUseCase(city.id)
            .onEach { isFav ->
                _state.update { it.copy(isFavourite = isFav) }
            }
            .launchIn(viewModelScope)
    }

    private fun loadForecast() {
        viewModelScope.launch {
            _state.update { it.copy(forecastState = DetailsState.ForecastState.Loading) }

            try {
                val forecast = getForecastUseCase(city.id)
                _state.update {
                    it.copy(forecastState = DetailsState.ForecastState.Loaded(forecast))
                }
            } catch (_: Exception) {
                _state.update {
                    it.copy(forecastState = DetailsState.ForecastState.Error)
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(city: City, index: Int): DetailsViewModel
    }
}