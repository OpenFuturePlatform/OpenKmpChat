package com.mutualmobile.harvestKmp.features.datamodels.userWalletDataModels

import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes.Screen.withRecipient
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes.Screen.withWalletDetail
import com.mutualmobile.harvestKmp.datamodel.ModalOpenCommand
import com.mutualmobile.harvestKmp.datamodel.NavigationOpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import com.mutualmobile.harvestKmp.di.SharedComponent
import com.mutualmobile.harvestKmp.di.StateUseCaseComponent
import com.mutualmobile.harvestKmp.di.UserWalletUseCaseComponent
import com.mutualmobile.harvestKmp.domain.model.Wallet
import com.mutualmobile.harvestKmp.domain.model.request.SaveWalletRequest
import com.mutualmobile.harvestKmp.domain.model.response.WalletResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class WalletsDataModel : OpenDataModel(), KoinComponent {
    private val _dataFlow = MutableSharedFlow<DataState>()
    val dataFlow = _dataFlow.asSharedFlow()

    private var currentLoadingJob: Job? = null

    private val walletLocal = SharedComponent().provideWalletLocal()
    private val walletRemote = UserWalletUseCaseComponent()

    private val walletRemoteSaveUseCase = walletRemote.provideSaveWalletUseCase()
    private val walletRemoteGetUseCase = walletRemote.provideGetWalletUseCase()

    private val stateUseCaseComponent = StateUseCaseComponent()
    private val getWalletBalanceUseCase = stateUseCaseComponent.provideGetBalanceUseCase()

    override fun activate() {
    }

    override fun destroy() {
        dataModelScope.cancel()
    }

    override fun refresh() {
    }

    fun getLocalUserWallets() {
        executeJob {
            _dataFlow.emit(LoadingState)
            val walletResponses = walletLocal.getAll().map {
                WalletResponse(
                    address = it.address,
                    privateKey = it.privateKey,
                    blockchainType = it.blockchainType,
                    balance = "0"
                )
            }
            _dataFlow.emit(SuccessState(walletResponses))

        }
    }

    fun saveWalletsLocal(inputs: List<Wallet>) {
        executeJob {
            _dataFlow.emit(LoadingState)
            println("Save Wallets locally: $inputs")
            for (input in inputs) {
                walletLocal.saveWallet(input)
            }
            _dataFlow.emit(SuccessState(inputs.map {
                WalletResponse(
                    address = it.address,
                    privateKey = it.privateKey,
                    blockchainType = it.blockchainType.name,
                    balance = "0"
                )
            }))
        }
    }

    fun saveWalletsRemote(input: Wallet) {
        executeJob {
            when (val response = walletRemoteSaveUseCase(
                SaveWalletRequest(input.blockchainType, input.address, input.userId)
            )) {

                is NetworkResponse.Success -> {
                    println("Saved remote")
                }

                is NetworkResponse.Failure -> {
                    _dataFlow.emit(ErrorState(response.throwable))
                }

                is NetworkResponse.Unauthorized -> handleUnauthorized()
            }
        }
    }

    fun navigateWalletDetail(address: String, encryptedPrivetKey: String, blockchainType: String) {

        executeJob {
            _dataFlow.emit(LoadingState)
            val screen =
                HarvestRoutes.Screen.WALLET_DETAIL.withWalletDetail(
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
