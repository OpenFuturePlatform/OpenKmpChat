package com.mutualmobile.harvestKmp.android.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mutualmobile.harvestKmp.datamodel.PraxisCommand
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel
import com.mutualmobile.harvestKmp.domain.model.Wallet
import com.mutualmobile.harvestKmp.domain.model.request.BlockchainType
import com.mutualmobile.harvestKmp.domain.model.request.DecryptWalletRequest
import com.mutualmobile.harvestKmp.domain.model.request.User
import com.mutualmobile.harvestKmp.domain.model.response.GetUserResponse
import com.mutualmobile.harvestKmp.domain.model.response.WalletResponse
import com.mutualmobile.harvestKmp.features.datamodels.userWalletDataModels.GetUserWalletsDataModel
import io.github.novacrypto.bip39.MnemonicGenerator
import io.github.novacrypto.bip39.Words
import io.github.novacrypto.bip39.wordlists.English
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.spongycastle.util.encoders.Base64
import wallet.core.jni.CoinType
import wallet.core.jni.HDWallet
import java.nio.charset.StandardCharsets
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and

class WalletScreenViewModel : ViewModel() {
    var walletListMap by mutableStateOf(emptyList<WalletResponse>())

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
    var currentWalletAddress by mutableStateOf("")
    var isWalletDetailDialogVisible by mutableStateOf(false)
    /**
     * The [User] in the chat room who is currently logged in, in other words the one using the app.
     */
    private val _localUser = MutableStateFlow(User())
    val localUser: StateFlow<User> = _localUser

    private val salt = "NaCl"
    private val initVector = "IIIIIIIIIIIIIIII"
    private val iterations = 1000
    private val keyLength = 256

    private val getUserWalletsDataModel = GetUserWalletsDataModel()

    init {
        with(getUserWalletsDataModel) {
            observeDataState()
            observeNavigationCommands()
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
                    val walletListMapNewState =
                        walletState.data as List<WalletResponse>

                    if (walletListMap.isEmpty())
                        walletListMap = walletListMapNewState
                    else if (walletListMap.none { walletResponse -> walletListMapNewState.contains(walletResponse) }) {
                        walletListMap = walletListMap.plus(walletListMapNewState)
                    }
                }

                else -> Unit
            }
        }.launchIn(viewModelScope)
    }

    fun getUserWallets(userState: PraxisDataModel.SuccessState<*>) {
        val userResponse = userState.data as GetUserResponse
        currentUserId = userResponse.id!!
//        getUserWalletsDataModel.getUserWallets(
//            userId = (userState.data as GetUserResponse).email ?: ""
//        )
        getUserWalletsDataModel.getLocalUserWallets()
    }

    fun generateWallet() {
        val seedPhrase = generateSeedCode()
        println("SeedPhrase: $seedPhrase")
        println("Blockchain: $blockchainType and password: $password")
        val hdWallet = HDWallet(seedPhrase, password)
        val coin: CoinType = when(blockchainType){
            BlockchainType.ETH -> CoinType.ETHEREUM
            BlockchainType.BTC -> CoinType.BITCOIN
            BlockchainType.BNB -> CoinType.BINANCE
        }
        println("Derivation path : ${coin.derivationPath()}")
        val address = hdWallet.getAddressForCoin(coin)
        println("Address: $address")
        val secretPrivateKey = hdWallet.getKeyForCoin(coin)
        val privateKey = secretPrivateKey.data().toHexString(false)
        println("Private key: $privateKey")
        val encrypted = encrypt(privateKey, password)
        println("Encrypted: $encrypted")
        val decrypted = decrypt(encrypted, password)
        println("Decrypted: $decrypted")
        val wallet = Wallet(address = address, privateKey = encrypted, userId = currentUserId, blockchainType = blockchainType)

        getUserWalletsDataModel.saveWalletLocal(wallet)
    }

    fun decryptWallet() {
        val decrypted = decrypt(currentWalletPrivateKey, password)
        println("Decrypted $decrypted")
        currentWalletDecryptedPrivateKey = decrypted
    }

    fun resetAll(onComplete: () -> Unit = {}) {
        walletListMap = emptyList()
        filteredWalletListMap = emptyList()
        textState = TextFieldValue("")
        onComplete()
    }

    private fun generateSeedCode(): String {
        val seedCode = StringBuilder()
        val entropy = ByteArray(Words.TWELVE.byteLength())
        SecureRandom().nextBytes(entropy)
        MnemonicGenerator(English.INSTANCE)
            .createMnemonic(entropy, seedCode::append)
        return seedCode.toString()
    }

    private fun encrypt(plainText: String, password: String): String{

        val passwordChars = password.toCharArray()
        val saltBytes = salt.toByteArray()

        val secretKey: SecretKey = getKey(passwordChars, saltBytes, iterations, keyLength)
        val key = secretKey.encoded

        return encrypt(key, initVector, plainText)!!
    }

    private fun decrypt(encryptedText: String, password: String): String {
        val passwordChars = password.toCharArray()
        val saltBytes = salt.toByteArray()
        val secretKey: SecretKey = getKey(passwordChars, saltBytes, iterations, keyLength)
        val key = secretKey.encoded
        return decrypt(key, initVector, encryptedText)!!
    }

    private fun getKey(password: CharArray?, salt: ByteArray?, iterations: Int, keyLength: Int): SecretKey {
        return try {
            val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val spec = PBEKeySpec(password, salt, iterations, keyLength)
            skf.generateSecret(spec)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: InvalidKeySpecException) {
            throw RuntimeException(e)
        }
    }

    private fun encrypt(key: ByteArray?, initVector: String, value: String): String? {
        try {
            val iv = IvParameterSpec(initVector.toByteArray())
            val skeySpec = SecretKeySpec(key, "AES")
            val cipher = Cipher.getInstance("AES/CTR/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)
            val encrypted = cipher.doFinal(value.toByteArray())
            return Base64.toBase64String(encrypted)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }

    private fun decrypt(key: ByteArray?, initVector: String, encrypted: String?): String? {
        try {
            val iv = IvParameterSpec(initVector.toByteArray(StandardCharsets.UTF_8))
            val skeySpec = SecretKeySpec(key, "AES")
            val cipher = Cipher.getInstance("AES/CTR/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv)
            val original = cipher.doFinal(Base64.decode(encrypted))
            return String(original)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }

    private fun ByteArray.toHexString(withPrefix: Boolean = true): String {
        val stringBuilder = StringBuilder()
        if(withPrefix) {
            stringBuilder.append("0x")
        }
        for (element in this) {
            stringBuilder.append(String.format("%02x", element and 0xFF.toByte()))
        }
        return stringBuilder.toString()
    }
}