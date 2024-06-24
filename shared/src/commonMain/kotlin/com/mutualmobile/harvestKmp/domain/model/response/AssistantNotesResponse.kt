package com.mutualmobile.harvestKmp.domain.model.response

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class AssistantNotesResponse(
    val chatId: Int?,
    val groupChatId: Int?,
    val members: List<String>?,
    val recipient: String?,
    val generatedAt: LocalDateTime,
    val version: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val notes: List<String>
)
