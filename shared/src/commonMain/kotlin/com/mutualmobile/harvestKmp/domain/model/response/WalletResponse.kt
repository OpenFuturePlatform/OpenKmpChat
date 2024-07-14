package com.mutualmobile.harvestKmp.domain.model.response

import com.mutualmobile.harvestKmp.domain.model.request.BlockchainType
import kotlinx.serialization.Serializable

@Serializable
data class WalletResponse(
    var address: String? = null,
    var privateKey: String? = null,
    var blockchainType: String? = null,
    var seedPhrases: String? = null,
)