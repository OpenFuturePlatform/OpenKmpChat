package com.mutualmobile.harvestKmp.domain.model.request

data class CreateWalletRequest(
    var blockchainType: BlockchainType,
    var password: String
)
