package ru.vsu.weatherapp.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.vsu.weatherapp.domain.entity.City
import ru.vsu.weatherapp.domain.usecase.local.AddCityToFavouriteUseCase
import ru.vsu.weatherapp.domain.usecase.remote.SearchCityUseCase

data class SearchUiState(
    val searchQuery: String = "",
    val searchState: SearchState = SearchState.Initial
) {
    sealed interface SearchState {
        data object Initial : SearchState
        data object Loading : SearchState
        data class SuccessLoaded(val cities: List<City>) : SearchState
        data object Error : SearchState
        data object EmptyResult : SearchState
    }
}

sealed interface SearchSideEffect {
    data object NavigateBack : SearchSideEffect
    data object SavedToFavourite : SearchSideEffect
    data class OpenForecast(val city: City) : SearchSideEffect
}

class SearchViewModel @AssistedInject constructor(
    @Assisted val openReason: OpenReason,
    private val searchCityUseCase: SearchCityUseCase,
    private val addCityToFavouriteUseCase: AddCityToFavouriteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    private val _searchQueryFlow = MutableStateFlow("")

    private val _sideEffects = Channel<SearchSideEffect>()
    val sideEffects = _sideEffects.receiveAsFlow()

    private var searchJob: Job? = null

    init {
        observeSearchQuery()
    }

    fun changeSearchQuery(newQuery: String) {
        searchJob?.cancel()

        _state.update {
            it.copy(
                searchQuery = newQuery,
                searchState = if (newQuery.isNotBlank()) {
                    SearchUiState.SearchState.Loading
                } else {
                    SearchUiState.SearchState.Initial
                }
            )
        }

        _searchQueryFlow.value = newQuery
    }

    fun onSearchClick() {
        performSearch(_state.value.searchQuery)
    }

    fun onBackClick() {
        viewModelScope.launch {
            _sideEffects.send(SearchSideEffect.NavigateBack)
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQueryFlow
                .debounce(450L)
                .distinctUntilChanged()
                .filter { it.isNotBlank() }
                .collectLatest { query ->
                    performSearch(query)
                }
        }
    }

    fun onCityClick(city: City) {
        viewModelScope.launch {
            when (openReason) {
                OpenReason.AddToFavourite -> {
                    addCityToFavouriteUseCase(city)
                    _sideEffects.send(SearchSideEffect.SavedToFavourite)
                }
                OpenReason.RegularSearch -> {
                    _sideEffects.send(SearchSideEffect.OpenForecast(city))
                }
            }
        }
    }

    private fun performSearch(query: String) {
        searchJob?.cancel()

        if (query.isBlank()) return

        searchJob = viewModelScope.launch {
            _state.update { it.copy(searchState = SearchUiState.SearchState.Loading) }
            try {
                val cities = searchCityUseCase(query)
                _state.update {
                    val searchState = if (cities.isEmpty()) {
                        SearchUiState.SearchState.EmptyResult
                    } else {
                        SearchUiState.SearchState.SuccessLoaded(cities)
                    }
                    it.copy(searchState = searchState)
                }
            } catch (e: Exception) {
                _state.update { it.copy(searchState = SearchUiState.SearchState.Error) }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(openReason: OpenReason): SearchViewModel
    }
}