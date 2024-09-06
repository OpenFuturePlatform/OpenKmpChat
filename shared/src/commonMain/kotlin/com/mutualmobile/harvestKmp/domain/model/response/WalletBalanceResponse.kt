package com.mutualmobile.harvestKmp.domain.model.response

import kotlinx.serialization.Serializable


@Serializable
data class WalletBalanceResponse(
    val address: String,
    val balance: Double,
    val blockchain: String
)
