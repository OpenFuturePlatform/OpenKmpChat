package com.mutualmobile.harvestKmp.domain.model.response

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class UserHomeChatMessageResponse(
    val uniqueId: String,
    val actor: String,
    val recipientName: String,
    val recipientAvatarUrl: String,
    val lastMessage: String,
    val lastAt: LocalDateTime
) {
}