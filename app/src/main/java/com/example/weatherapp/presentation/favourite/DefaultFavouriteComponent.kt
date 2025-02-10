package com.example.weatherapp.presentation.favourite

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.example.weatherapp.domain.entity.City
import com.example.weatherapp.presentation.extensions.componentScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class DefaultFavouriteComponent @Inject constructor(
    private val favouriteStoreFactory: FavouriteStoreFactory,
    private val onCityItemClicked: (City) -> Unit,
    private val onSearchClicked: () -> Unit,
    private val onAddToFavouriteClicked: () -> Unit,
    componentContext: ComponentContext
) : FavouriteComponent, ComponentContext by componentContext {

    val store = instanceKeeper.getStore { favouriteStoreFactory.create() }


    init {
        store.labels.onEach {
            when(it){

                is FavouriteStore.Label.ClickCityItem -> {
                    onCityItemClicked(it.city)
                }

                FavouriteStore.Label.ClickSearch -> {
                    onSearchClicked()
                }

                FavouriteStore.Label.ClickAddToFavourite -> {
                    onAddToFavouriteClicked()
                }
            }
        }.launchIn(componentScope())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<FavouriteStore.State> = store.stateFlow

    override fun onClickSearch() {
        store.accept(FavouriteStore.Intent.ClickSearch)
    }

    override fun onClickAddFavourite() {
        store.accept(FavouriteStore.Intent.ClickAddToFavourite)
    }

    override fun onClickCityItem(city: City) {
        store.accept(FavouriteStore.Intent.ClickCityItem(city))
    }
}