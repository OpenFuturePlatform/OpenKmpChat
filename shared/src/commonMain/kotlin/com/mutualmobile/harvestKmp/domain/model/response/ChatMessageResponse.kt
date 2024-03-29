package com.mutualmobile.harvestKmp.domain.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageResponse(
    val id: String,
    val sender: String,
    val recipient: String,
    val content: String,
    val receivedAt: String
)