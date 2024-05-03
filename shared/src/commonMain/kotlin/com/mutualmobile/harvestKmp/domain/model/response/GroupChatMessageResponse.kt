package com.mutualmobile.harvestKmp.domain.model.response

import com.mutualmobile.harvestKmp.domain.model.TextType
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class GroupChatMessageResponse(
    val id: String,
    val sender: String,
    val groupChatId: String,
    val content: String,
    val contentType: TextType,
    val sentAt: LocalDateTime
)