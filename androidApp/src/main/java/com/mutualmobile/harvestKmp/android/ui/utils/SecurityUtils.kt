package com.mutualmobile.harvestKmp.android.ui.utils

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
import java.math.BigDecimal
import java.math.BigInteger
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

    fun generateWallet(blockchainType: BlockchainType, seedPhrase: String, password: String, userId: String): Wallet {
        //todo - we can use the seed phrase to generate the private key and address
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
        val encrypted = encrypt(privateKey, password)
        println("Encrypted: $encrypted")
        //val decrypted = decrypt(encrypted, password)
        //println("Decrypted: $decrypted")

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

    private fun encrypt(plainText: String, password: String): String {

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