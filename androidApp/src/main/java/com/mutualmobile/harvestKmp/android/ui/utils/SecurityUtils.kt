package com.mutualmobile.harvestKmp.android.ui.utils

import com.mutualmobile.harvestKmp.domain.model.Wallet
import com.mutualmobile.harvestKmp.domain.model.request.BlockchainType
import io.github.novacrypto.bip39.MnemonicGenerator
import io.github.novacrypto.bip39.Words
import io.github.novacrypto.bip39.wordlists.English
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


object SecurityUtils {

    private const val salt = "NaCl"
    private const val initVector = "IIIIIIIIIIIIIIII"
    private const val iterations = 1000
    private const val keyLength = 256

    fun generateWallet(blockchainType: BlockchainType, password: String, userId: String): Wallet {
        val seedPhrase = generateSeedCode()
        println("SeedPhrase: $seedPhrase")
        println("Blockchain: $blockchainType and password: $password")
        val hdWallet = HDWallet(seedPhrase, "")
        val coin: CoinType = when (blockchainType) {
            BlockchainType.ETH -> CoinType.ETHEREUM
            BlockchainType.BTC -> CoinType.BITCOIN
            BlockchainType.BNB -> CoinType.BINANCE
            BlockchainType.TRX -> CoinType.TRON
        }
        println("Derivation path : ${coin.derivationPath()}")
        val address = hdWallet.getAddressForCoin(coin)
        println("Address: $address")
        val secretPrivateKey = hdWallet.getKeyForCoin(coin)
        val privateKey = secretPrivateKey.data().toHexString(false)
        println("Private key: $privateKey")
        val encrypted = encrypt(privateKey, password)
        println("Encrypted: $encrypted")
        //val decrypted = decrypt(encrypted, password)
        //println("Decrypted: $decrypted")

        return Wallet(address = address, privateKey = encrypted, userId = userId, blockchainType = blockchainType)
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

    fun decrypt(encryptedText: String, password: String): String {
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