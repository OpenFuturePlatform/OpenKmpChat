package com.mutualmobile.harvestKmp.domain.model.request


enum class BlockchainType(
    private val id: Int,
    private val value: String
) {
    ETH(60, "ETH"),
    BTC(0, "BTC"),
    BNB(714, "BNB"),
    TRX(195, "TRX"),
    SOL(501, "SOL"),
    USDT(1, "USDT")
}