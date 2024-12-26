package com.mutualmobile.harvestKmp.android.ui.utils

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.google.protobuf.ByteString
import com.mutualmobile.harvestKmp.domain.model.Wallet
import com.mutualmobile.harvestKmp.domain.model.request.BlockchainType
import io.github.novacrypto.bip39.MnemonicGenerator
import io.github.novacrypto.bip39.Words
import io.github.novacrypto.bip39.wordlists.English
import org.spongycastle.util.encoders.Base64
import wallet.core.java.AnySigner
import wallet.core.jni.CoinType
import wallet.core.jni.HDWallet
import wallet.core.jni.PrivateKey
import wallet.core.jni.proto.Ethereum
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and


object SecurityUtils {

    private const val salt = "NaCl"
    private const val initVector = "IIIIIIIIIIIIIIII"
    private const val iterations = 1000
    private const val keyLength = 256

    private const val ANDROID_KEY_STORE = "AndroidKeyStore"
    private const val AES_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
    private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
    private const val PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
    private const val TRANSFORMATION = "$AES_ALGORITHM/$BLOCK_MODE/$PADDING"

    private var iv: ByteArray = byteArrayOf(55, 54, 53, 52, 51, 50, 49, 48, 47, 46, 45, 44)
    val knownSecret = "KnownSecretValue"

    fun generateWallet(blockchainType: BlockchainType, seedPhrase: String, password: String, userId: String): Wallet {
        println("Generating wallet for blockchain type: $blockchainType")
        val hdWallet = HDWallet(seedPhrase, "")
        val coin = when (blockchainType) {
            BlockchainType.ETH -> CoinType.ETHEREUM
            BlockchainType.BTC -> CoinType.BITCOIN
            BlockchainType.BNB -> CoinType.SMARTCHAIN
            BlockchainType.TRX -> CoinType.TRON
            BlockchainType.SOL -> CoinType.SOLANA
            BlockchainType.USDT -> CoinType.TRON
        }
        println("Derivation path : ${coin.derivationPath()}")
        val address = hdWallet.getAddressForCoin(coin)
        println("Address: $address")
        val secretPrivateKey = hdWallet.getKeyForCoin(coin)
        val privateKey = secretPrivateKey.data().toHexString(false)
        println("Private key: $privateKey")
        //val encrypted = encrypt(privateKey, password)
        val encrypted = encryptRaw(privateKey)
        println("Encrypted: $encrypted")
        val decrypted = decryptRaw(encrypted)
        println("Decrypted: $decrypted")

        return Wallet(
            address = address,
            privateKey = encrypted,
            userId = userId,
            blockchainType = blockchainType,
            seedPhrase = seedPhrase
        )
    }

    fun generateSeedCode(): String {
        val seedCode = StringBuilder()
        val entropy = ByteArray(Words.TWELVE.byteLength())
        SecureRandom().nextBytes(entropy)
        MnemonicGenerator(English.INSTANCE)
            .createMnemonic(entropy, seedCode::append)
        return seedCode.toString()
    }

    fun getPrivateKeyFromStr(privateKey: String): PrivateKey {
        return PrivateKey(privateKey.hexStringToByteArray())
    }

    fun encryptWithPassword(plainText: String, password: String): String {
        val passwordChars = password.toCharArray()
        val secretKey: SecretKey = getKeyByPassword(passwordChars)
        val key = secretKey.encoded
        return encrypt(key, plainText)!!
    }

    fun decryptWithPassword(encryptedText: String, password: String): String {
        val passwordChars = password.toCharArray()
        val secretKey: SecretKey = getKeyByPassword(passwordChars)
        val key = secretKey.encoded
        return decrypt(key, encryptedText)!!
    }

    private fun getKeyByPassword(password: CharArray?): SecretKey {
        return try {
            val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val saltBytes = salt.toByteArray()
            val spec = PBEKeySpec(password, saltBytes, iterations, keyLength)
            skf.generateSecret(spec)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: InvalidKeySpecException) {
            throw RuntimeException(e)
        }
    }

    private fun encrypt(key: ByteArray?, value: String): String? {
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

    private fun decrypt(key: ByteArray?, encrypted: String?): String? {
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

    /*
    * HERE START NEW ENCRYPT/DECRYPT METHODS WITH AES/GCM/NoPadding
    *
    * */
    fun encryptRaw(value: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey("harvestKey"))
        val encryptedBytes = cipher.doFinal(value.toByteArray())  // Encrypt the data
        val iv = cipher.iv
        val combinedData = iv + encryptedBytes // Prepend IV to the encrypted data
        return String(Base64.encode(combinedData)) // Encode to Base64 using Spongy Castle
    }

    fun decryptRaw(encrypted: String): String {
        val decodedData =
            Base64.decode(encrypted.replace("\\s".toRegex(), "+")) // Decode from Base64 using Spongy Castle
        //println("Decoded data: ${String(decodedData)}")
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val iv = decodedData.copyOfRange(0, 12) // Extract IV (first 12 bytes)
        val encryptedBytes = decodedData.copyOfRange(12, decodedData.size) // Extract encrypted data (the rest)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey("harvestKey"), GCMParameterSpec(128, iv))
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes)
    }

    fun generateSecretKey(keyAlias: String): SecretKey {
        println("Generating key for $keyAlias ...")
        val keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM, ANDROID_KEY_STORE)

        val spec = KeyGenParameterSpec.Builder(keyAlias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(BLOCK_MODE)
            .setEncryptionPaddings(PADDING)
            .setUserAuthenticationRequired(false)
            .setRandomizedEncryptionRequired(true)
            .build()

        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    fun getSecretKey(keyAlias: String): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore.load(null)
        val existingKey = keyStore.getEntry(keyAlias, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: generateSecretKey(keyAlias)
    }

    // Function to encrypt the known secret with the PIN code
    fun encryptSecretWithPin(pinCode: String, secret: String): String {
        println("Encrypting secret with pin code: $pinCode and secret: $secret")
        val key = pinCode.toByteArray(Charsets.UTF_8)
        val keySpec = SecretKeySpec(key, "AES")
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv = ByteArray(12)
        SecureRandom().nextBytes(iv)
        val gcmParameterSpec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec)
        val encryptedSecret = cipher.doFinal(secret.toByteArray(Charsets.UTF_8))
        val combinedData = iv + encryptedSecret
        return String(Base64.encode(combinedData)) // Encode to Base64
    }

    // Function to decrypt the secret with the PIN code
    fun decryptSecretWithPin(pinCode: String, encryptedData: String): String? {
        val key = pinCode.toByteArray(Charsets.UTF_8)
        val keySpec = SecretKeySpec(key, "AES")
        val decodedData = Base64.decode(encryptedData)
        val iv = decodedData.copyOfRange(0, 12)
        val encryptedBytes = decodedData.copyOfRange(12, decodedData.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val gcmParameterSpec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec)

        return try {
            String(cipher.doFinal(encryptedBytes), Charsets.UTF_8)
        } catch (e: Exception) {
            null // Decryption failed
        }
    }

    fun saveEncryptedSecret(context: Context, encryptedSecret: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("encryptedSecret", encryptedSecret)
        editor.putBoolean("isPinSet", true)
        editor.apply()
    }

    fun clearPreferences(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("encryptedSecret")
        editor.remove("isPinSet")
        editor.remove("isAuthenticated")
        editor.apply()
    }

    fun getEncryptedSecret(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("encryptedSecret", null)
    }

    fun isPinSet(context: Context): Boolean {
        return getEncryptedSecret(context) != null
    }

    fun signEthereum(
        receiverAddress: String,
        secretPrivateKey: PrivateKey,
        gasPriceInteger: BigInteger,
        gasLimitInteger: BigInteger,
        amountInteger: BigInteger,
        addressNonce: BigInteger
    ): String {
        val signerInput = Ethereum.SigningInput.newBuilder().apply {
            chainId = ByteString.copyFrom(BigInteger("11155111").toByteArray())
            gasPrice = gasPriceInteger.toByteString()
            gasLimit = gasLimitInteger.toByteString()
            toAddress = receiverAddress
            nonce = ByteString.copyFrom(addressNonce.toByteArray())
            transaction = Ethereum.Transaction.newBuilder().apply {
                transfer = Ethereum.Transaction.Transfer.newBuilder().apply {
                    amount = amountInteger.toByteString()
                }.build()
            }.build()
            privateKey = ByteString.copyFrom(secretPrivateKey.data())
        }.build()
        val output = AnySigner.sign(signerInput, CoinType.ETHEREUM, Ethereum.SigningOutput.parser())
        println("Signed transaction: \n${output.encoded.toByteArray().toHexString()}")
        return output.encoded.toByteArray().toHexString()
    }

    private fun BigInteger.toByteString(): ByteString {
        return ByteString.copyFrom(this.toByteArray())
    }

    private fun ByteArray.toHexString(withPrefix: Boolean = true): String {
        val stringBuilder = StringBuilder()
        if (withPrefix) {
            stringBuilder.append("0x")
        }
        for (element in this) {
            stringBuilder.append(String.format("%02x", element and 0xFF.toByte()))
        }
        return stringBuilder.toString()
    }

    private fun String.hexStringToByteArray(): ByteArray {
        val HEX_CHARS = "0123456789ABCDEF"
        val result = ByteArray(length / 2)
        for (i in 0 until length step 2) {
            val firstIndex = HEX_CHARS.indexOf(this[i].toUpperCase());
            val secondIndex = HEX_CHARS.indexOf(this[i + 1].toUpperCase());
            val octet = firstIndex.shl(4).or(secondIndex)
            result.set(i.shr(1), octet.toByte())
        }
        return result
    }
}