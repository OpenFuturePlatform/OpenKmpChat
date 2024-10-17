package com.mutualmobile.harvestKmp.features.datamodels.userWalletDataModels

import com.mutualmobile.harvestKmp.datamodel.ModalPraxisCommand
import com.mutualmobile.harvestKmp.datamodel.NavigationPraxisCommand
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel
import com.mutualmobile.harvestKmp.di.StateUseCaseComponent
import com.mutualmobile.harvestKmp.domain.model.request.BalanceRequest
import com.mutualmobile.harvestKmp.features.NetworkResponse
import io.ktor.websocket.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class GetStateGasLimitDataModel() :
    PraxisDataModel(), KoinComponent {
    private val _dataFlow = MutableSharedFlow<DataState>()
    val dataFlow = _dataFlow.asSharedFlow()

    private var currentLoadingJob: Job? = null

    private val stateUseCaseComponent = StateUseCaseComponent()
    private val getGasLimitUseCase = stateUseCaseComponent.provideGetGasLimitUseCase()

    override fun activate() {
    }

    override fun destroy() {
        dataModelScope.cancel()
    }

    override fun refresh() {
    }

    fun getGasLimit(address: String, blockchainType: String) {
        currentLoadingJob?.cancel()
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)
            when (val response = getGasLimitUseCase(
                BalanceRequest(
                    address = address,
                    blockchainName = blockchainType,
                    contractAddress = null
                )
            )) {

                is NetworkResponse.Success -> {
                    println("GetGasLimit: Success -> ${response.data}")
                    _dataFlow.emit(SuccessState(response.data))
                }

                is NetworkResponse.Failure -> {
                    _dataFlow.emit(ErrorState(response.throwable))
                }

                is NetworkResponse.Unauthorized -> {
                    settings.clear()
                    intPraxisCommand.emit(ModalPraxisCommand("Unauthorized", "Please login again!"))
                    intPraxisCommand.emit(NavigationPraxisCommand(""))
                }

            }
        }
    }

    suspend fun getGasLimitSync(address: String, blockchainType: String) : Long {
        val response = getGasLimitUseCase(
            BalanceRequest(
                address = address,
                blockchainName = blockchainType,
                contractAddress = null
            )
        )
        return when (response) {

            is NetworkResponse.Success -> {
                response.data
            }

            is NetworkResponse.Failure -> {
                0
            }

            is NetworkResponse.Unauthorized -> {
                0
            }
        }
    }
}
