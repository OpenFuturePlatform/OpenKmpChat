package com.mutualmobile.harvestKmp.domain.model.response

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class WalletResponse(
    var address: String? = null,
    var privateKey: String? = null,
    var blockchainType: String? = null,
    var seedPhrases: String? = null,
    var balance: String?
)

@Serializable
data class ContractResponse(
    var address: String? = null,
    var name: String? = null,
    var blockchainType: String? = null
)

@Serializable
data class TransactionResponse(
    val hash: String,
    val from: Set<String>,
    val to: String,
    val amount: Double,
    val date: LocalDateTime,
    val blockHeight: Long,
    val blockHash: String,
    val rate: Double?,
    val native: Boolean?,
    val token: String?
)