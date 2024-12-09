package com.mutualmobile.harvestKmp.android.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mutualmobile.harvestKmp.datamodel.OpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import com.mutualmobile.harvestKmp.domain.model.response.ExchangeRate
import com.mutualmobile.harvestKmp.domain.model.response.TransactionResponse
import com.mutualmobile.harvestKmp.domain.model.response.WalletBalanceResponse
import com.mutualmobile.harvestKmp.features.datamodels.userWalletDataModels.GetStateBalanceDataModel
import com.mutualmobile.harvestKmp.features.datamodels.userWalletDataModels.GetStateRatesDataModel
import com.mutualmobile.harvestKmp.features.datamodels.userWalletDataModels.GetStateTransactionsDataModel
import com.mutualmobile.harvestKmp.features.datamodels.userWalletDataModels.WalletDetailDataModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class WalletSenderDetailScreenViewModel : ViewModel() {
    var currentNavigationCommand: OpenCommand? by mutableStateOf(null)
    private var currentScreenState: OpenDataModel.DataState by mutableStateOf(OpenDataModel.EmptyState)
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    var address by mutableStateOf("")
    var password by mutableStateOf("")
    var privateKey by mutableStateOf("")
    var blockchainType by mutableStateOf("")
    var decryptedPrivateKey by mutableStateOf("")

    private var exchangeRate by mutableStateOf(emptyList<ExchangeRate>())
    private var walletBalance by mutableStateOf(0.0)

    // TRANSACTIONS
    private var walletTransactions by mutableStateOf(emptyList<TransactionResponse>())

    private val walletDetailDataModel = WalletDetailDataModel()

    private val getStateRatesDataModel = GetStateRatesDataModel()
    private val getStateBalanceDataModel = GetStateBalanceDataModel()

    init {
        println("WalletSenderDetailScreenViewModel init")

        with(walletDetailDataModel) {
            observeDataState()
            observeNavigationCommands()
        }

        with(getStateRatesDataModel) {
            observeStateDataState()
            observeStateNavigationCommands()
        }
        with(getStateBalanceDataModel) {
            observeStateBalanceDataState()
            observeStateBalanceNavigationCommands()
        }

    }

    private fun WalletDetailDataModel.observeNavigationCommands() =
        praxisCommand.onEach { newCommand ->
            println("WalletDetailScreenViewModel observeNavigationCommands $newCommand")
            currentNavigationCommand = newCommand
        }.launchIn(viewModelScope)

    private fun WalletDetailDataModel.observeDataState() {

        dataFlow.onEach { walletDetailState ->
            currentScreenState = walletDetailState
            when (walletDetailState) {
                is OpenDataModel.SuccessState<*> -> {
                    println("Get Wallet Detail State $walletDetailState")
                }
                is OpenDataModel.ErrorState -> {
                    println("Get Wallet Detail State Error: ${walletDetailState.throwable}")
                }
                else -> Unit
            }
        }.launchIn(viewModelScope)
    }

    private fun GetStateRatesDataModel.observeStateNavigationCommands() =
        praxisCommand.onEach { newCommand ->
            currentNavigationCommand = newCommand
        }.launchIn(viewModelScope)

    private fun GetStateRatesDataModel.observeStateDataState() {
        dataFlow.onEach { rateState ->
            currentScreenState = rateState
            when (rateState) {
                is OpenDataModel.SuccessState<*> -> {
                    println("Rates in wallet detail: ${rateState.data}")
                    exchangeRate = rateState.data as List<ExchangeRate>
                }
                else -> Unit
            }
        }.launchIn(viewModelScope)
    }

    private fun GetStateBalanceDataModel.observeStateBalanceNavigationCommands() =
        praxisCommand.onEach { newCommand ->
            currentNavigationCommand = newCommand
        }.launchIn(viewModelScope)

    private fun GetStateBalanceDataModel.observeStateBalanceDataState() {
        dataFlow.onEach { balanceState ->
            currentScreenState = balanceState
            println("BalanceState $balanceState")
            when (balanceState) {
                is OpenDataModel.SuccessState<*> -> {
                    val balanceResponse = balanceState.data as WalletBalanceResponse
                    walletBalance = balanceResponse.balance
                }

                else -> Unit
            }
        }.launchIn(viewModelScope)
    }

    fun resetAll(onComplete: () -> Unit = {}) {
        address = ""
        privateKey = ""
        decryptedPrivateKey = ""
        walletTransactions = listOf()
        blockchainType = ""
        currentNavigationCommand = null
        currentScreenState = OpenDataModel.EmptyState
        onComplete()
    }
}