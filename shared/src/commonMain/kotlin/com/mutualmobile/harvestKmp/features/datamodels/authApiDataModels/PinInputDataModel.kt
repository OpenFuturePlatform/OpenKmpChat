package com.mutualmobile.harvestKmp.features.datamodels.authApiDataModels

import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes
import com.mutualmobile.harvestKmp.datamodel.NavigationOpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class PinInputDataModel :
    OpenDataModel(), KoinComponent {

    private val _dataFlow = MutableSharedFlow<DataState>()
    val dataFlow = _dataFlow.asSharedFlow()

    override fun activate() {
    }

    override fun destroy() {
        dataModelScope.cancel()
    }

    override fun refresh() {

    }

    fun checkPin(pinCode: String) {
        dataModelScope.launch(exceptionHandler) {
            _dataFlow.emit(LoadingState)
            intOpenCommand.emit(
                NavigationOpenCommand(
                    screen = HarvestRoutes.Screen.CHAT
                )
            )
        }
    }
}
