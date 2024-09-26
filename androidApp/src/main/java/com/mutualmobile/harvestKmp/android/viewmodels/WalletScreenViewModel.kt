package com.mutualmobile.harvestKmp.android.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mutualmobile.harvestKmp.android.ui.utils.SecurityUtils
import com.mutualmobile.harvestKmp.datamodel.PraxisCommand
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel
import com.mutualmobile.harvestKmp.domain.model.request.BlockchainType
import com.mutualmobile.harvestKmp.domain.model.response.*
import com.mutualmobile.harvestKmp.features.datamodels.userWalletDataModels.GetStateBalanceDataModel
import com.mutualmobile.harvestKmp.features.datamodels.userWalletDataModels.GetStateRatesDataModel
import com.mutualmobile.harvestKmp.features.datamodels.userWalletDataModels.GetUserWalletsDataModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class WalletScreenViewModel : ViewModel() {
    var wallets by mutableStateOf(emptyList<WalletResponse>())
    var contracts by mutableStateOf(emptyList<ContractResponse>())
    var exchangeRates by mutableStateOf(emptyList<CoinGateRate>())
    var walletBalances by mutableStateOf(mapOf<String, String>())
    var filteredWalletListMap: List<WalletResponse> = emptyList()

    var textState by mutableStateOf(TextFieldValue(""))

    var currentWalletScreenState: PraxisDataModel.DataState by mutableStateOf(PraxisDataModel.EmptyState)

    var walletScreenNavigationCommands: PraxisCommand? by mutableStateOf(null)

    var isWalletGenerateDialogVisible by mutableStateOf(false)
    var blockchainType by mutableStateOf(BlockchainType.ETH)
    var password by mutableStateOf("")
    var currentUserId by mutableStateOf("")

    var currentWalletPrivateKey by mutableStateOf("")
    var currentWalletDecryptedPrivateKey by mutableStateOf("")
    var currentWalletSeeedPhrases by mutableStateOf("")
    var currentWalletAddress by mutableStateOf("")
    var isWalletDetailDialogVisible by mutableStateOf(false)

    private val getUserWalletsDataModel = GetUserWalletsDataModel()
    private val getStateRatesDataModel = GetStateRatesDataModel()
    private val getStateBalanceDataModel = GetStateBalanceDataModel()

    init {
        with(getUserWalletsDataModel) {
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

    private fun GetUserWalletsDataModel.observeNavigationCommands() {
        praxisCommand.onEach { newCommand ->
            walletScreenNavigationCommands = newCommand
        }.launchIn(viewModelScope)
    }

    private fun GetUserWalletsDataModel.observeDataState() {
        dataFlow.onEach { walletState ->
            currentWalletScreenState = walletState
            when (walletState) {
                is PraxisDataModel.SuccessState<*> -> {
                    println("WalletState $walletState")
                    val walletListMapNewState = walletState.data as List<WalletResponse>

                    if (wallets.isEmpty())
                        wallets = walletListMapNewState
                    else if (wallets.none { walletResponse -> walletListMapNewState.contains(walletResponse) }) {
                        wallets = wallets.plus(walletListMapNewState)
                    }
                }

                else -> Unit
            }
        }.launchIn(viewModelScope)
    }

    private fun GetStateRatesDataModel.observeStateNavigationCommands() {
        praxisCommand.onEach { newCommand ->
            walletScreenNavigationCommands = newCommand
        }.launchIn(viewModelScope)
    }

    private fun GetStateRatesDataModel.observeStateDataState() {
        dataFlow.onEach { rateState ->
            currentWalletScreenState = rateState
            when (rateState) {
                is PraxisDataModel.SuccessState<*> -> {
                    println("Rates ${rateState.data}")
                    exchangeRates = rateState.data as List<CoinGateRate>
                }

                else -> Unit
            }
        }.launchIn(viewModelScope)
    }

    private fun GetStateBalanceDataModel.observeStateBalanceNavigationCommands() {
        praxisCommand.onEach { newCommand ->
            walletScreenNavigationCommands = newCommand
        }.launchIn(viewModelScope)
    }

    private fun GetStateBalanceDataModel.observeStateBalanceDataState() {
        dataFlow.onEach { balanceState ->
            currentWalletScreenState = balanceState
            when (balanceState) {
                is PraxisDataModel.SuccessState<*> -> {
                    println("Balance ${balanceState.data}")
                    val balanceResponse = balanceState.data as WalletBalanceResponse
                    val key = balanceResponse.address + balanceResponse.blockchain
                    val balance = balanceResponse.balance
                    walletBalances = walletBalances.plus(Pair(key, balance.toString()))
                }

                else -> Unit
            }
        }.launchIn(viewModelScope)
    }

    fun getUserWallets(userState: PraxisDataModel.SuccessState<*>) {
        val userResponse = userState.data as GetUserResponse
        currentUserId = userResponse.id!!
        getUserWalletsDataModel.getLocalUserWallets()
        //getUserWalletsDataModel.getUserWallets(userResponse.email!!)
        getStateRatesDataModel.getCryptoRates()
        wallets.forEach{
            println("Fetch balance ${it.address} and ${it.blockchainType}")
            if (it.blockchainType!! == BlockchainType.BNB.name || it.blockchainType == BlockchainType.ETH.name || it.blockchainType == BlockchainType.TRX.name) {
                getStateBalanceDataModel.getCryptoBalance(
                    address = it.address!!,
                    contractAddress = null,
                    blockchainType = it.blockchainType!!
                )
            }
        }
    }

    fun generateWallet() {
        val wallet = SecurityUtils.generateWallet(blockchainType, password, currentUserId)
        getUserWalletsDataModel.saveWalletLocal(wallet)
        getUserWalletsDataModel.saveWalletRemote(wallet)
    }

    fun decryptWallet() {
        val decrypted = SecurityUtils.decrypt(currentWalletPrivateKey, password)
        println("Decrypted $decrypted")
        currentWalletDecryptedPrivateKey = decrypted
    }

    fun resetAll(onComplete: () -> Unit = {}) {
        wallets = emptyList()
        filteredWalletListMap = emptyList()
        textState = TextFieldValue("")
        onComplete()
    }
}