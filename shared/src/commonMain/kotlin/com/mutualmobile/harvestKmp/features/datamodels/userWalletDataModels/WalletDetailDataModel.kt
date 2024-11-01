package com.mutualmobile.harvestKmp.features.datamodels.userWalletDataModels

import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes.Screen.withWalletDetail
import com.mutualmobile.harvestKmp.datamodel.ModalOpenCommand
import com.mutualmobile.harvestKmp.datamodel.NavigationOpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import com.mutualmobile.harvestKmp.di.StateUseCaseComponent
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class WalletDetailDataModel : OpenDataModel(), KoinComponent {
    private val _dataFlow = MutableSharedFlow<DataState>()
    val dataFlow = _dataFlow.asSharedFlow()

    private var currentLoadingJob: Job? = null

    var address = MutableStateFlow("")
    var password = MutableStateFlow("")
    var privateKey = MutableStateFlow("")
    var blockchainType = MutableStateFlow("")

    private val stateUseCaseComponent = StateUseCaseComponent()
    private val getWalletBalanceUseCase = stateUseCaseComponent.provideGetBalanceUseCase()
    private val getRatesUseCase = stateUseCaseComponent.provideGetRatesUseCase()
    private val getRateUseCase = stateUseCaseComponent.provideGetRateUseCase()

    override fun activate() {
        println("GetWalletDetailDataModel activated")
    }

    override fun destroy() {
        dataModelScope.cancel()
    }

    override fun refresh() {
    }

    fun navigateWalletSenderDetail(address: String, encryptedPrivetKey: String, blockchainType: String) {
        executeJob {
            _dataFlow.emit(LoadingState)
            val screen =
                HarvestRoutes.Screen.WALLET_SENDER_DETAIL.withWalletDetail(
                    address,
                    blockchainType,
                    encryptedPrivetKey
                )
            println("navigation screen: $screen")
            intOpenCommand.emit(
                NavigationOpenCommand(
                    screen = screen
                )
            )
        }
    }

    fun navigateWalletReceiverDetail(address: String, encryptedPrivetKey: String, blockchainType: String) {
        executeJob {
            _dataFlow.emit(LoadingState)
            val screen =
                HarvestRoutes.Screen.WALLET_RECEIVER_DETAIL.withWalletDetail(
                    address,
                    blockchainType,
                    encryptedPrivetKey
                )
            println("navigation screen: $screen")
            intOpenCommand.emit(
                NavigationOpenCommand(
                    screen = screen
                )
            )
        }
    }

    private suspend fun handleUnauthorized() {
        settings.clear()
        intOpenCommand.emit(ModalOpenCommand("Unauthorized", "Please login again!"))
        intOpenCommand.emit(NavigationOpenCommand(HarvestRoutes.Screen.LOGIN))
    }

    private fun executeJob(block: suspend () -> Unit) {
        currentLoadingJob?.cancel()
        currentLoadingJob = dataModelScope.launch { block() }
    }
}
