package com.mutualmobile.harvestKmp.domain.model.response

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class AssistantTodosResponse(
    val chatId: Int?,
    val groupChatId: Int?,
    val members: List<String>?,
    val recipient: String?,
    val generatedAt: LocalDateTime,
    val version: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val todos: List<Todo>
)

@Serializable
data class Todo(
    val executor: String?,
    val description: String?,
    val dueDate: LocalDateTime?,
    val context: String?
)
