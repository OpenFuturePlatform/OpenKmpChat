package com.mutualmobile.harvestKmp.domain.model.request

import kotlinx.serialization.Serializable

@Serializable
data class SaveWalletRequest(
    var blockchainType: BlockchainType,
    var address: String,
    var userId: String
)
