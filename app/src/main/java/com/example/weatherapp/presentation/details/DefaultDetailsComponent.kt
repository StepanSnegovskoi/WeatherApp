package com.example.weatherapp.presentation.details

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

class DefaultDetailsComponent @Inject constructor(
    private val storeFactory: DetailsStoreFactory,
    private val onBackClicked: () -> Unit,
    private val city: City,
    componentContext: ComponentContext
) : DetailsComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore { storeFactory.create(city) }

    init {
        store.labels.onEach {
            when(it){
                DetailsStore.Label.ClickBack -> {
                    onBackClicked()
                }
            }
        }.launchIn(componentScope())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<DetailsStore.State> = store.stateFlow

    override fun onClickBack() {
        store.accept(DetailsStore.Intent.ClickBack)
    }

    override fun onClickFavouriteStatus() {
        store.accept(DetailsStore.Intent.ClickChangeFavouriteStatus)
    }
}