package com.mutualmobile.harvestKmp.domain.model.request

import com.mutualmobile.harvestKmp.domain.model.TextType
import kotlinx.serialization.Serializable

@Serializable
data class GroupChatMessageRequest (
    val sender: String,
    val groupId: String,
    val body: String,
    val contentType: TextType
)