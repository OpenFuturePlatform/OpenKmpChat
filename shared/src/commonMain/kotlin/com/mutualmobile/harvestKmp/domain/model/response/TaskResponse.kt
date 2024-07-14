package com.mutualmobile.harvestKmp.domain.model.response

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class TaskResponse(
    val id: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val assignor: String? ,
    var assignee: String? ,
    var taskTitle: String? ,
    var taskDescription: String? ,
    var taskDate: LocalDate?
)