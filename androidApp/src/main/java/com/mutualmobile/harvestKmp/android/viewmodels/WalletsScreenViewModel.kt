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
import com.mutualmobile.harvestKmp.domain.model.Wallet
import com.mutualmobile.harvestKmp.domain.model.request.BlockchainType
import com.mutualmobile.harvestKmp.domain.model.response.*
import com.mutualmobile.harvestKmp.features.datamodels.userWalletDataModels.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

class WalletsScreenViewModel : ViewModel() {
    var currentWalletsScreenState: OpenDataModel.DataState by mutableStateOf(OpenDataModel.EmptyState)
    var currentNavigationCommand: OpenCommand? by mutableStateOf(null)

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    var wallets by mutableStateOf(emptyList<WalletResponse>())
    var contracts by mutableStateOf(emptyList<ContractResponse>())
    var exchangeRates by mutableStateOf(emptyList<CoinGateRate>())
    var walletBalances by mutableStateOf(mapOf<String, String>())
    var filteredWalletListMap: List<WalletResponse> = emptyList()

    var textState by mutableStateOf(TextFieldValue(""))

    var isWalletGenerateDialogVisible by mutableStateOf(false)
    var blockchainNetworks by mutableStateOf(emptyList<BlockchainType>())
    var password by mutableStateOf("")
    var currentUserId by mutableStateOf("")

    var currentWalletPrivateKey by mutableStateOf("")
    var currentWalletDecryptedPrivateKey by mutableStateOf("")
    var currentWalletSeeedPhrases by mutableStateOf("")
    var currentWalletAddress by mutableStateOf("")
    var isWalletDetailDialogVisible by mutableStateOf(false)
    var isWalletDecryptDialogVisible by mutableStateOf(false)
    var isWalletTransactionDialogVisible by mutableStateOf(false)
    var currentReceiverAddress by mutableStateOf("")
    var currentReceiverAmount by mutableStateOf("")
    var currentBroadcastHash by mutableStateOf("")
    var currentBroadcastError by mutableStateOf("")
    var isBroadcastLoading by mutableStateOf(false)

    private val getWalletsDataModel = WalletsDataModel()
    private val getStateRatesDataModel = GetStateRatesDataModel()
    private val getStateBalanceDataModel = GetStateBalanceDataModel()
    private val getStateGasPriceDataModel = GetStateGasPriceDataModel()
    private val getStateGasLimitDataModel = GetStateGasLimitDataModel()
    private val getStateNonceDataModel = GetStateNonceDataModel()
    private val postBroadcastDataModel = PostStateBroadcastDataModel()

    init {
        println("WalletsScreenViewModel init")
        with(getWalletsDataModel) {
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
        with(postBroadcastDataModel) {
            observeDataState()
            observeNavigationCommands()
        }
    }

    private fun WalletsDataModel.observeNavigationCommands() =
        praxisCommand.onEach { newCommand ->
            currentNavigationCommand = newCommand
        }.launchIn(viewModelScope)
    private fun WalletsDataModel.observeDataState() {
        dataFlow.onEach { walletState ->
            currentWalletsScreenState = walletState
            when (walletState) {
                is OpenDataModel.SuccessState<*> -> {
                    //println("User WalletState $walletState")
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
    private fun GetStateRatesDataModel.observeStateNavigationCommands() =
        praxisCommand.onEach { newCommand ->
            println("Rates Command $newCommand")
            currentNavigationCommand = newCommand
        }.launchIn(viewModelScope)
    private fun GetStateRatesDataModel.observeStateDataState() {
        dataFlow.onEach { rateState ->
            currentWalletsScreenState = rateState
            when (rateState) {
                is OpenDataModel.SuccessState<*> -> {
                    println("Rates ${rateState.data}")
                    exchangeRates = rateState.data as List<CoinGateRate>
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
            currentWalletsScreenState = balanceState
            //println("BalanceState $balanceState")
            when (balanceState) {
                is OpenDataModel.SuccessState<*> -> {
                    val balanceResponse = balanceState.data as WalletBalanceResponse
                    val key = balanceResponse.address + balanceResponse.blockchain
                    val balance = balanceResponse.balance
                    //println("Key: $key and balance: $balance")
                    walletBalances = walletBalances.plus(Pair(key, balance.toString()))
                }

                else -> Unit
            }
        }.launchIn(viewModelScope)
    }
    private fun PostStateBroadcastDataModel.observeNavigationCommands() =
        praxisCommand.onEach { newCommand ->
            currentNavigationCommand = newCommand
        }.launchIn(viewModelScope)
    private fun PostStateBroadcastDataModel.observeDataState() {
        dataFlow.onEach { broadcastState ->
            currentWalletsScreenState = broadcastState
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
                    currentWalletPrivateKey = ""
                    currentWalletDecryptedPrivateKey = ""
                    currentWalletAddress = ""
                    currentWalletSeeedPhrases = ""
                }

                else -> Unit
            }
        }.launchIn(viewModelScope)
    }

    fun getUserWallets(userState: OpenDataModel.SuccessState<*>) {
        val userResponse = userState.data as GetUserResponse
        currentUserId = userResponse.id!!
        getWalletsDataModel.getLocalUserWallets()
        //getUserWalletsDataModel.getUserWallets(userResponse.email!!)
        getStateRatesDataModel.getCryptoRates(null)
        wallets.forEach {
            //println("Fetch balance ${it.address} and ${it.blockchainType}  = ${it.balance}")
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
        val seedPhrases = SecurityUtils.generateSeedCode()
        println("SeedPhrase: $seedPhrases and network: $blockchainNetworks")
        var currentWallets = emptyList<Wallet>()
        for (blockchainType in blockchainNetworks) {
            val wallet = SecurityUtils.generateWallet(blockchainType, seedPhrases, password, currentUserId)
            currentWallets = currentWallets.plus(wallet)

        }
        currentWallets.forEach { wallet ->
            getWalletsDataModel.saveWalletsRemote(wallet)
        }
        getWalletsDataModel.saveWalletsLocal(currentWallets)

    }

    fun broadcastTransaction(
        fromAddress: String,
        privateKey: String,
        receiverAddress: String,
        receiverAmount: String
    ) {

        val privKey = SecurityUtils.getPrivateKeyFromStr(privateKey)
        //val receiverAddress = "0x014D9Fcdb245CF31BfbaD92F3031FE036fE91Bc3"
        //val amount = java.math.BigInteger("45000000000000") // 0.000045 ETH
        val amount = BlockchainUtils.toWei(receiverAmount, BlockchainUtils.Unit.ETHER).toBigInteger()
        println("Sign transaction fromAddress: $fromAddress and privateKey: $privateKey and amount: $amount")
        runBlocking {
            val gasPrice = getStateGasPriceDataModel.getGasPriceSync(fromAddress, BlockchainType.ETH.name)
            var gasLimit = getStateGasLimitDataModel.getGasLimitSync(fromAddress, BlockchainType.ETH.name)
            val nonce = getStateNonceDataModel.getEthNonceSync(fromAddress, BlockchainType.ETH.name)

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

    fun decryptWallet() {
        val decrypted = SecurityUtils.decrypt(currentWalletPrivateKey, password)
        println("Decrypted $decrypted")
        currentWalletDecryptedPrivateKey = decrypted
    }

    fun onWalletClicked(address : String, encryptedPrivetKey: String, blockchainType: String) {
        getWalletsDataModel.navigateWalletDetail(address, encryptedPrivetKey, blockchainType)
    }

    fun resetAll(onComplete: () -> Unit = {}) {
        currentNavigationCommand = null
        currentWalletsScreenState = OpenDataModel.EmptyState
        onComplete()
    }
}