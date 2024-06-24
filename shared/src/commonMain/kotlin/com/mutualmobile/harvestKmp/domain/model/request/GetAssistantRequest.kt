package com.mutualmobile.harvestKmp.domain.model.request

import kotlinx.serialization.Serializable


@Serializable
data class GetAssistantRequest(
    val chatId: String,
    val isGroup: Boolean
)
