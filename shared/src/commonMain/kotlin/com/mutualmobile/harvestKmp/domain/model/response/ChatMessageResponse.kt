package com.mutualmobile.harvestKmp.domain.model.response

import com.mutualmobile.harvestKmp.domain.model.TextType
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageResponse(
    val id: String,
    val sender: String,
    val recipient: String,
    val content: String,
    val contentType: TextType,
    val receivedAt: LocalDateTime
)