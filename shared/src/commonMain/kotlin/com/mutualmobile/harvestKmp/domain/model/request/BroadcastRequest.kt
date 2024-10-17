package com.mutualmobile.harvestKmp.domain.model.request

import kotlinx.serialization.Serializable


@Serializable
data class BroadcastRequest(
    val blockchainName: String,
    val signature: String
)
