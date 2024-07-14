package com.mutualmobile.harvestKmp.features.datamodels.userWalletDataModels

import com.mutualmobile.harvestKmp.datamodel.ModalPraxisCommand
import com.mutualmobile.harvestKmp.datamodel.NavigationPraxisCommand
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel
import com.mutualmobile.harvestKmp.di.SharedComponent
import com.mutualmobile.harvestKmp.di.UserWalletUseCaseComponent
import com.mutualmobile.harvestKmp.domain.model.request.CreateWalletRequest
import com.mutualmobile.harvestKmp.domain.model.request.DecryptWalletRequest
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

    private val salt = "NaCl"
    private val initVector = "IIIIIIIIIIIIIIII"
    private val iterations = 1000
    private val keyLength = 256
    private val walletLocal = SharedComponent().provideWalletLocal()

    private val userWalletUseCaseComponent = UserWalletUseCaseComponent()
    private val getUserWalletsUseCase = userWalletUseCaseComponent.provideGetWalletUseCase()
    private val generateWalletUseCase = userWalletUseCaseComponent.provideGenerateWalletUseCase()
    private val decryptWalletUseCase = userWalletUseCaseComponent.provideDecryptWalletUseCase()

    override fun activate() {
    }

    override fun destroy() {
        dataModelScope.cancel()
    }

    override fun refresh() {
    }

    fun getUserWallets(
        userId: String?
    ) {
        currentLoadingJob?.cancel()
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)
            when (val response =
                getUserWalletsUseCase(
                    username = userId!!
                )) {

                is NetworkResponse.Success -> {
                    print("Wallet Response $response")
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

    fun generateWallets(
        createWalletRequest: CreateWalletRequest
    ) {
        currentLoadingJob?.cancel()
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)

            when (val response =
                generateWalletUseCase(
                    createWalletRequest = createWalletRequest
                )) {
                is NetworkResponse.Success -> {
                    _dataFlow.emit(SuccessState(listOf(response.data)))
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

    fun decryptWallet(
        decryptWalletRequest: DecryptWalletRequest
    ) {
        currentLoadingJob?.cancel()
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)
            when (val response =
                decryptWalletUseCase(
                    decryptWalletRequest = decryptWalletRequest
                )) {
                is NetworkResponse.Success -> {
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
}