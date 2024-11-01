package com.mutualmobile.harvestKmp.domain.model.response

import kotlinx.serialization.Serializable


@Serializable
data class CoinGateRate(
    val btc: CoinGateExchangeRate,
    val bnb: CoinGateExchangeRate,
    val trx: CoinGateExchangeRate,
    val sol: CoinGateExchangeRate,
    val eth: CoinGateExchangeRate,
    val usdt: CoinGateExchangeRate
)

@Serializable
data class ExchangeRate(
    val symbol: String,
    val price: String
)

@Serializable
data class CoinGateExchangeRate(
    val usd: String
)