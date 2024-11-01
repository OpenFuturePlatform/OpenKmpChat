package com.mutualmobile.harvestKmp.features.datamodels.userWalletDataModels

import com.mutualmobile.harvestKmp.datamodel.ModalOpenCommand
import com.mutualmobile.harvestKmp.datamodel.NavigationOpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import com.mutualmobile.harvestKmp.di.StateUseCaseComponent
import com.mutualmobile.harvestKmp.features.NetworkResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class GetStateRatesDataModel() :
    OpenDataModel(), KoinComponent {
    private val _dataFlow = MutableSharedFlow<DataState>()
    val dataFlow = _dataFlow.asSharedFlow()

    private var currentLoadingJob: Job? = null


    private val stateUseCaseComponent = StateUseCaseComponent()
    private val getWalletRatesUseCase = stateUseCaseComponent.provideGetRatesUseCase()
    private val getWalletRateUseCase = stateUseCaseComponent.provideGetRateUseCase()

    override fun activate() {
    }

    override fun destroy() {
        dataModelScope.cancel()
    }

    override fun refresh() {
    }
    fun getCryptoRates(_ticker: String?) {
        currentLoadingJob?.cancel()
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)
            when (val response = getWalletRatesUseCase(ticker = _ticker)) {

                is NetworkResponse.Success -> {
                    println("Rate now: ${response.data}")
                    _dataFlow.emit(SuccessState(listOf(response.data)))
                }

                is NetworkResponse.Failure -> {
                    _dataFlow.emit(ErrorState(response.throwable))
                }

                is NetworkResponse.Unauthorized -> {
                    settings.clear()
                    intOpenCommand.emit(ModalOpenCommand("Unauthorized", "Please login again!"))
                    intOpenCommand.emit(NavigationOpenCommand(""))
                }

            }
        }
    }

    fun getCryptoRate(_ticker: String) {
        currentLoadingJob?.cancel()
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)
            when (val response = getWalletRateUseCase(ticker = _ticker)) {

                is NetworkResponse.Success -> {
                    _dataFlow.emit(SuccessState(listOf(response.data)))
                }

                is NetworkResponse.Failure -> {
                    _dataFlow.emit(ErrorState(response.throwable))
                }

                is NetworkResponse.Unauthorized -> {
                    settings.clear()
                    intOpenCommand.emit(ModalOpenCommand("Unauthorized", "Please login again!"))
                    intOpenCommand.emit(NavigationOpenCommand(""))
                }

            }
        }
    }
}
