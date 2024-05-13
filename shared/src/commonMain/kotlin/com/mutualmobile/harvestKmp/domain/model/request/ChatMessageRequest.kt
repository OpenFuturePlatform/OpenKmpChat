package com.mutualmobile.harvestKmp.domain.model.request

import com.mutualmobile.harvestKmp.domain.model.TextType
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageRequest (
    val sender: String,
    val recipient: String?,
    val body: String,
    val contentType: TextType
)