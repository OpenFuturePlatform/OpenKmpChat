package com.mutualmobile.harvestKmp.domain.model.request

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable


@Serializable
data class AssistantRequest(
    val chatId: String,
    val isGroup: Boolean,
    val startTime: LocalDateTime?,
    val endTime: LocalDateTime?
)
