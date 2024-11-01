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

class GetStateTransactionsDataModel :
    OpenDataModel(), KoinComponent {
    private val _dataFlow = MutableSharedFlow<DataState>()
    val dataFlow = _dataFlow.asSharedFlow()

    private var currentLoadingJob: Job? = null

    private val stateUseCaseComponent = StateUseCaseComponent()
    private val getWalletTransactionsUseCase = stateUseCaseComponent.provideGetTransactionsUseCase()

    override fun activate() {
    }

    override fun destroy() {
        dataModelScope.cancel()
    }

    override fun refresh() {
    }

    fun getTransactions(address: String, blockchainType: String) {
        currentLoadingJob?.cancel()
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)
            when (val response = getWalletTransactionsUseCase(
                BalanceRequest(
                    address = address,
                    contractAddress = null,
                    blockchainName = blockchainType
                )
            )) {

                is NetworkResponse.Success -> {
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
}
