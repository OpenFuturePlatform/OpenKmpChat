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
import com.mutualmobile.harvestKmp.domain.model.Wallet
import com.mutualmobile.harvestKmp.domain.model.request.BlockchainType
import com.mutualmobile.harvestKmp.domain.model.response.*
import com.mutualmobile.harvestKmp.features.datamodels.userWalletDataModels.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.spongycastle.util.test.FixedSecureRandom.BigInteger

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
    var blockchainNetworks by mutableStateOf(emptyList<BlockchainType>())
    var password by mutableStateOf("")
    var currentUserId by mutableStateOf("")

    var currentWalletPrivateKey by mutableStateOf("")
    var currentWalletDecryptedPrivateKey by mutableStateOf("")
    var currentWalletSeeedPhrases by mutableStateOf("")
    var currentWalletAddress by mutableStateOf("")
    var isWalletDetailDialogVisible by mutableStateOf(false)
    var currentBlockchainGasPrice by mutableStateOf(Long.MIN_VALUE)
    var currentBlockchainGasLimit by mutableStateOf(Long.MIN_VALUE)
    var currentBlockchainAddressNonce by mutableStateOf(Long.MIN_VALUE)

    private val getUserWalletsDataModel = GetUserWalletsDataModel()
    private val getStateRatesDataModel = GetStateRatesDataModel()
    private val getStateBalanceDataModel = GetStateBalanceDataModel()
    private val getStateGasPriceDataModel = GetStateGasPriceDataModel()
    private val getStateGasLimitDataModel = GetStateGasLimitDataModel()
    private val getStateNonceDataModel = GetStateNonceDataModel()
    init {
        with(getUserWalletsDataModel) {
            observeDataState()
            observeNavigationCommands()
        }
        with(getStateRatesDataModel) {
            observeStateDataState()
            observeStateNavigationCommands()
        }
        with(getStateGasPriceDataModel){
            observeStateGasPriceDataState()
        }
        with(getStateGasLimitDataModel){
            observeStateGasLimitDataState()
        }
        with(getStateNonceDataModel){
            observeStateNonceDataState()
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

    private fun GetStateGasPriceDataModel.observeStateGasPriceDataState() {
        dataFlow.onEach { gasPriceState ->

            when (gasPriceState) {
                is PraxisDataModel.SuccessState<*> -> {
                    println("Gas Limit ${gasPriceState.data}")
                    currentBlockchainGasPrice = gasPriceState.data as Long
                }

                else -> Unit
            }
        }.launchIn(viewModelScope)
    }

    private fun GetStateNonceDataModel.observeStateNonceDataState() {
        dataFlow.onEach { nonceState ->

            when (nonceState) {
                is PraxisDataModel.SuccessState<*> -> {
                    println("Nonce ${nonceState.data}")
                    currentBlockchainGasPrice = nonceState.data as Long
                }

                else -> Unit
            }
        }.launchIn(viewModelScope)
    }

    private fun GetStateGasLimitDataModel.observeStateGasLimitDataState() {
        dataFlow.onEach { gasLimitState ->

            when (gasLimitState) {
                is PraxisDataModel.SuccessState<*> -> {
                    println("Gas Limit ${gasLimitState.data}")
                    currentBlockchainGasLimit = gasLimitState.data as Long
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
            println("BalanceState $balanceState")
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
                getStateGasLimitDataModel.getGasLimit(it.address!!, it.blockchainType!!)
                getStateGasPriceDataModel.getGasPrice(it.address!!, it.blockchainType!!)
            }
        }
    }

    fun generateWallet() {
        val seedPhrases = SecurityUtils.generateSeedCode()
        println("SeedPhrase: $seedPhrases and network: $blockchainNetworks")
        var currentWallets = emptyList<Wallet>()
        for (blockchainType in blockchainNetworks) {
            val wallet = SecurityUtils.generateWallet(blockchainType, seedPhrases, password, currentUserId)
            currentWallets = currentWallets.plus(wallet)

        }
        currentWallets.forEach { wallet ->
            getUserWalletsDataModel.saveWalletsRemote(wallet)
        }
        getUserWalletsDataModel.saveWalletsLocal(currentWallets)

    }

    fun signEthereumTransaction(fromAddress: String, privateKey: String) {
        println("Sign transaction fromAddres: $fromAddress and privateKey: $privateKey")
        val privKey = SecurityUtils.getPrivateKeyFromStr(privateKey)
        val receiverAddress = "0x014D9Fcdb245CF31BfbaD92F3031FE036fE91Bc3"
        val amount = java.math.BigInteger("1000000000000000000")

        getStateGasPriceDataModel.getGasPrice(fromAddress, BlockchainType.ETH.name)
        getStateGasLimitDataModel.getGasLimit(fromAddress, BlockchainType.ETH.name)

//        val signature = SecurityUtils.signEthereum(
//            receiverAddress,
//            privKey,
//            java.math.BigInteger(currentBlockchainGasPrice),
//            java.math.BigInteger(currentBlockchainGasPrice),
//            amount
//        )

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