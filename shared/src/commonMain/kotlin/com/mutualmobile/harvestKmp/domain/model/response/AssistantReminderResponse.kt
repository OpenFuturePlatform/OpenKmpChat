package com.mutualmobile.harvestKmp.domain.model.response

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class AssistantReminderResponse(
    val chatId: Int?,
    val groupChatId: Int?,
    val members: List<String>?,
    val recipient: String?,
    val generatedAt: LocalDateTime,
    val version: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val reminders: List<ReminderItem>
)

@Serializable
data class ReminderItem(
    val remindAt: LocalDateTime?,
    val description: String?
)