package com.mutualmobile.harvestKmp.domain.model.response

import kotlinx.serialization.Serializable


@Serializable
data class CoinGateRate(
    val btc: CoinGateExchangeRate,
    val bnb: CoinGateExchangeRate,
    val trx: CoinGateExchangeRate,
    val eth: CoinGateExchangeRate
)

@Serializable
data class CoinGateExchangeRate(
    val usdt: String
)