package com.mutualmobile.harvestKmp.features.datamodels.userWalletDataModels

import com.mutualmobile.harvestKmp.datamodel.ModalPraxisCommand
import com.mutualmobile.harvestKmp.datamodel.NavigationPraxisCommand
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel
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

class GetUserWalletsDataModel() :
    PraxisDataModel(), KoinComponent {
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
        currentLoadingJob?.cancel()
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)
            val walletResponses = walletLocal.getAll().map {
                WalletResponse(
                    address = it.address,
                    privateKey = it.privateKey,
                    blockchainType = it.blockchainType,
                    balance = "0"
                )
            }
            /*val walletResponses = walletLocal
                .getAll()
                .map {
                    when (val response =
                        getWalletBalanceUseCase(
                            request = WalletBalanceRequest(
                                address = it.address!!,
                                blockchain = BlockchainType.valueOf(it.blockchainType!!),
                                isNative = true
                            )
                        )) {

                        is NetworkResponse.Success -> {
                            WalletResponse(
                                address = it.address,
                                privateKey = it.privateKey,
                                blockchainType = it.blockchainType,
                                balance = response.data.balance.toString()
                            )
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
                }*/
            _dataFlow.emit(SuccessState(walletResponses))

        }
    }

    fun saveWalletLocal(input: Wallet) {
        currentLoadingJob?.cancel()
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)
            walletLocal.saveWallet(input)
            _dataFlow.emit(
                SuccessState(
                    listOf(
                        WalletResponse(
                            address = input.address,
                            blockchainType = input.blockchainType.name,
                            privateKey = input.privateKey,
                            balance = null
                        )
                    )
                )
            )
        }
    }

    fun saveWalletRemote(input: Wallet) {
        currentLoadingJob?.cancel()
        currentLoadingJob = dataModelScope.launch {

            when (val response = walletRemoteSaveUseCase(
                SaveWalletRequest(input.blockchainType, input.address, input.userId)
            )) {

                is NetworkResponse.Success -> {
                   println("Saved remote")
                    _dataFlow.emit(
                        SuccessState(
                            listOf(
                                WalletResponse(
                                    address = input.address,
                                    blockchainType = input.blockchainType.name,
                                    privateKey = input.privateKey,
                                    balance = null
                                )
                            )
                        )
                    )
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
}
