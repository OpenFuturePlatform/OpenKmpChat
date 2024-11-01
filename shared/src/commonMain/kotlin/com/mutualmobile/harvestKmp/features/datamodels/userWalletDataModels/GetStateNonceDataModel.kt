package com.mutualmobile.harvestKmp.features.datamodels.userWalletDataModels

import com.mutualmobile.harvestKmp.datamodel.ModalOpenCommand
import com.mutualmobile.harvestKmp.datamodel.NavigationOpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import com.mutualmobile.harvestKmp.di.StateUseCaseComponent
import com.mutualmobile.harvestKmp.domain.model.request.BalanceRequest
import com.mutualmobile.harvestKmp.features.NetworkResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class GetStateNonceDataModel() :
    OpenDataModel(), KoinComponent {
    private val _dataFlow = MutableSharedFlow<DataState>()
    val dataFlow = _dataFlow.asSharedFlow()

    private var currentLoadingJob: Job? = null


    private val stateUseCaseComponent = StateUseCaseComponent()
    private val getNonceUseCase = stateUseCaseComponent.provideGetNonceUseCase()

    override fun activate() {
    }

    override fun destroy() {
        dataModelScope.cancel()
    }

    override fun refresh() {
    }

    fun getEthNonce(address: String, blockchainType: String) {
        currentLoadingJob?.cancel()
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)
            when (val response = getNonceUseCase(
                BalanceRequest(
                    address = address,
                    blockchainName = blockchainType,
                    contractAddress = null
                )
            )) {

                is NetworkResponse.Success -> {
                    println("GetNonce: Success -> ${response.data}")
                    _dataFlow.emit(SuccessState(response.data))
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

    suspend fun getEthNonceSync(address: String, blockchainType: String): Long {
        val response =
            getNonceUseCase(BalanceRequest(address = address, blockchainName = blockchainType, contractAddress = null))
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
