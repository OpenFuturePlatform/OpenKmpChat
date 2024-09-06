package com.mutualmobile.harvestKmp.domain.model.request


enum class BlockchainType(
    private val id: Int,
    private val value: String
) {
    ETH(1, "ETH"),
    BTC(2, "BTC"),
    BNB(3, "BNB"),
    TRX(4, "TRX")
}