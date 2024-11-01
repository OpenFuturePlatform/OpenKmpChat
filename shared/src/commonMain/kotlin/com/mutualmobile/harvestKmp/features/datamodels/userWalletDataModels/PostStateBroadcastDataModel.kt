package com.mutualmobile.harvestKmp.features.datamodels.userWalletDataModels

import com.mutualmobile.harvestKmp.datamodel.ModalOpenCommand
import com.mutualmobile.harvestKmp.datamodel.NavigationOpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import com.mutualmobile.harvestKmp.di.StateUseCaseComponent
import com.mutualmobile.harvestKmp.domain.model.request.BroadcastRequest
import com.mutualmobile.harvestKmp.features.NetworkResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class PostStateBroadcastDataModel() :
    OpenDataModel(), KoinComponent {
    private val _dataFlow = MutableSharedFlow<DataState>()
    val dataFlow = _dataFlow.asSharedFlow()

    private var currentLoadingJob: Job? = null

    private val stateUseCaseComponent = StateUseCaseComponent()
    private val getBroadcastUseCase = stateUseCaseComponent.provideBroadcastUseCase()

    override fun activate() {
    }

    override fun destroy() {
        dataModelScope.cancel()
    }

    override fun refresh() {
    }

    fun createBroadcast(signature: String, blockchainType: String) {
        currentLoadingJob?.cancel()
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)
            when (val response = getBroadcastUseCase(
                BroadcastRequest(
                    signature = signature,
                    blockchainName = blockchainType
                )
            )) {

                is NetworkResponse.Success -> {
                    println("GetBroadcast: Success -> ${response.data}")
                    _dataFlow.emit(SuccessState(response.data))
                }

                is NetworkResponse.Failure -> {
                    println("GetBroadcast: Error -> ${response.throwable}")
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

    suspend fun createBroadcastSync(signature: String, blockchainType: String): String {
        val response =
            getBroadcastUseCase(BroadcastRequest(signature = signature, blockchainName = blockchainType))
        return when (response) {

            is NetworkResponse.Success -> {
                response.data
            }

            is NetworkResponse.Failure -> {
                ""
            }

            is NetworkResponse.Unauthorized -> {
                ""
            }

        }
    }
}
