package com.mutualmobile.harvestKmp.domain.model.request

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageRequest (
    val sender: String,
    val receiver: String,
    val body: String
)