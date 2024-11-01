package com.mutualmobile.harvestKmp.android.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mutualmobile.harvestKmp.android.ui.utils.BlockchainUtils
import com.mutualmobile.harvestKmp.android.ui.utils.SecurityUtils
import com.mutualmobile.harvestKmp.datamodel.OpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import com.mutualmobile.harvestKmp.domain.model.Message
import com.mutualmobile.harvestKmp.domain.model.request.BlockchainType
import com.mutualmobile.harvestKmp.domain.model.response.*
import com.mutualmobile.harvestKmp.features.datamodels.userWalletDataModels.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

class WalletReceiverDetailScreenViewModel : ViewModel() {
    var currentNavigationCommand: OpenCommand? by mutableStateOf(null)
    var currentScreenState: OpenDataModel.DataState by mutableStateOf(OpenDataModel.EmptyState)
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    var address by mutableStateOf("")
    var password by mutableStateOf("")
    var privateKey by mutableStateOf("")
    var blockchainType by mutableStateOf("")

    var exchangeRate by mutableStateOf(emptyList<ExchangeRate>())
    var walletBalance by mutableStateOf(0.0)

    var isWalletDecryptDialogVisible by mutableStateOf(false)

    var currentWalletAddress by mutableStateOf("")
    var currentWalletDecryptedPrivateKey by mutableStateOf("")
    // TRANSACTIONS
    var walletTransactions by mutableStateOf(emptyList<TransactionResponse>())

    // TRANSACTION BROADCAST
    var currentReceiverAddress by mutableStateOf("")
    var currentReceiverAmount by mutableStateOf("")
    var currentBroadcastHash by mutableStateOf("")
    var currentBroadcastError by mutableStateOf("")
    var isBroadcastLoading by mutableStateOf(false)

    private val walletDetailDataModel = WalletDetailDataModel()

    private val getStateRatesDataModel = GetStateRatesDataModel()
    private val getStateBalanceDataModel = GetStateBalanceDataModel()
    private val getStateTransactionsDataModel = GetStateTransactionsDataModel()

    private val getStateGasPriceDataModel = GetStateGasPriceDataModel()
    private val getStateGasLimitDataModel = GetStateGasLimitDataModel()
    private val getStateNonceDataModel = GetStateNonceDataModel()
    private val postBroadcastDataModel = PostStateBroadcastDataModel()

    init {
        println("WalletDetailScreenViewModel init")

        with(walletDetailDataModel) {
            observeDataState()
            observeNavigationCommands()
        }
        with(postBroadcastDataModel) {
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
        with(getStateTransactionsDataModel) {
            observeStateBalanceDataState()
            observeStateBalanceNavigationCommands()
        }
    }

    private fun PostStateBroadcastDataModel.observeDataState() {
        dataFlow.onEach { broadcastState ->
            currentScreenState = broadcastState
            when (broadcastState) {
                is OpenDataModel.SuccessState<*> -> {
                    println("Broadcast State $broadcastState")
                    currentBroadcastHash = broadcastState.data as String
                    isBroadcastLoading = false
                }

                is OpenDataModel.ErrorState -> {
                    println("Broadcast State Error: ${broadcastState.throwable}")

                    isBroadcastLoading = false
                    currentBroadcastError = broadcastState.throwable.message!!

                    currentReceiverAmount = ""
                    currentReceiverAddress = ""

                }

                else -> Unit
            }
        }.launchIn(viewModelScope)
    }

    private fun PostStateBroadcastDataModel.observeNavigationCommands() {
        praxisCommand.onEach { newCommand ->
            currentNavigationCommand = newCommand
        }.launchIn(viewModelScope)
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

    private fun GetStateTransactionsDataModel.observeStateBalanceNavigationCommands() =
        praxisCommand.onEach { newCommand ->
            currentNavigationCommand = newCommand
        }.launchIn(viewModelScope)

    private fun GetStateTransactionsDataModel.observeStateBalanceDataState() {
        dataFlow.onEach { transactionState ->
            currentScreenState = transactionState
            println("TransactionState $transactionState")
            when (transactionState) {
                is OpenDataModel.SuccessState<*> -> {
                    val transactionResponse = transactionState.data as List<TransactionResponse>
                    walletTransactions = transactionResponse
                }

                else -> Unit
            }

        }.launchIn(viewModelScope)
    }

    fun decryptWallet() {
        val decrypted = SecurityUtils.decrypt(privateKey, password)
        println("Decrypted $decrypted")
        currentWalletDecryptedPrivateKey = decrypted
        isWalletDecryptDialogVisible = false
    }

    fun broadcastTransaction(
        receiverAddress: String,
        receiverAmount: String
    ) {

        val privKey = SecurityUtils.getPrivateKeyFromStr(currentWalletDecryptedPrivateKey)
        val amount = BlockchainUtils.toWei(receiverAmount, BlockchainUtils.Unit.ETHER).toBigInteger()
        println("Sign transaction fromAddress: $address and privateKey: $currentWalletDecryptedPrivateKey and amount: $amount")
        runBlocking {
            val gasPrice = getStateGasPriceDataModel.getGasPriceSync(address, BlockchainType.ETH.name)
            var gasLimit = getStateGasLimitDataModel.getGasLimitSync(address, BlockchainType.ETH.name)
            val nonce = getStateNonceDataModel.getEthNonceSync(address, BlockchainType.ETH.name)

            println("GasPrice: $gasPrice and GasLimit: $gasLimit and nonce: $nonce")
            if (gasLimit > 21_000L) {
                gasLimit = 21_000L
            }

            val signature = SecurityUtils.signEthereum(
                receiverAddress,
                privKey,
                gasPrice.toBigInteger(),
                gasLimit.toBigInteger(),
                amount,
                nonce.toBigInteger()
            )

            postBroadcastDataModel.createBroadcast(signature, BlockchainType.ETH.name)

        }
    }

    fun getWalletDetail(_address: String, _privateKey: String, _blockchainType: String) {
        address = _address
        privateKey = _privateKey
        blockchainType = _blockchainType
        currentWalletAddress = _address
    }

    fun resetAll(onComplete: () -> Unit = {}) {
        address = ""
        privateKey = ""
        currentWalletDecryptedPrivateKey = ""
        walletTransactions = listOf()
        blockchainType = ""
        currentNavigationCommand = null
        currentScreenState = OpenDataModel.EmptyState
        onComplete()
    }
}