package ru.vsu.weatherapp.presentation.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import ru.vsu.weatherapp.presentation.details.DetailsScreen
import ru.vsu.weatherapp.presentation.details.DetailsViewModel
import ru.vsu.weatherapp.presentation.favourite.FavouriteScreen
import ru.vsu.weatherapp.presentation.favourite.FavouriteViewModel
import ru.vsu.weatherapp.presentation.search.OpenReason
import ru.vsu.weatherapp.presentation.search.SearchScreen
import ru.vsu.weatherapp.presentation.search.SearchViewModel

@Composable
fun WeatherApp(
    detailsViewModelFactory: DetailsViewModel.Factory,
    searchViewModelFactory: SearchViewModel.Factory,
    navigationViewModel: NavigationViewModel = hiltViewModel()
) {
    val navState by navigationViewModel.navigationState.collectAsStateWithLifecycle()

    val backStack = remember(navState.backStack) {
        navState.backStack.toMutableList()
    }

    BackHandler(enabled = backStack.size > 1) {
        navigationViewModel.navigateBack()
    }

    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        onBack = {
            navigationViewModel.navigateBack()
        },
        entryProvider = entryProvider {

            entry<WeatherRoute.Favourite> {
                val viewModel: FavouriteViewModel = hiltViewModel()
                
                FavouriteScreen(
                    viewModel = viewModel,
                    onCityItemClicked = { city, id ->
                        navigationViewModel.navigateTo(WeatherRoute.Details(city, id))
                    },
                    onSearchClicked = {
                        navigationViewModel.navigateTo(WeatherRoute.Search(OpenReason.RegularSearch))
                    },
                    onAddToFavouriteClicked = {
                        navigationViewModel.navigateTo(WeatherRoute.Search(OpenReason.AddToFavourite))
                    }
                )
            }

            entry<WeatherRoute.Search> { route ->
                val viewModel: SearchViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return searchViewModelFactory.create(route.reason) as T
                        }
                    }
                )

                SearchScreen(
                    viewModel = viewModel,
                    onBackClicked = { navigationViewModel.navigateBack() },
                    onCitySavedToFavourite = { navigationViewModel.navigateBack() },
                    onForecastForCityRequested = { city ->
                        navigationViewModel.navigateTo(WeatherRoute.Details(city, 1))
                    }
                )
            }

            entry<WeatherRoute.Details> { route ->
                val viewModel: DetailsViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return detailsViewModelFactory.create(route.city, route.index) as T
                        }
                    }
                )

                DetailsScreen(
                    viewModel = viewModel,
                    onBackClick = { navigationViewModel.navigateBack() }
                )
            }
        }
    )
}