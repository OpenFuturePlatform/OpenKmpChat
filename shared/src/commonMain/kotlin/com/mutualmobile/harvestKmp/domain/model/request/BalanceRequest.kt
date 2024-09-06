package com.mutualmobile.harvestKmp.domain.model.request

import kotlinx.serialization.Serializable


@Serializable
data class BalanceRequest(
    val blockchainName: String,
    val isNative: Boolean,
    val address: String
)
