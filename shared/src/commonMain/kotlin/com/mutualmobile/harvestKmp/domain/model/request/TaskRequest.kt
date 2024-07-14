package com.mutualmobile.harvestKmp.domain.model.request

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable


@Serializable
data class TaskRequest(
    val assignor: String? ,
    var assignee: String? ,
    var taskTitle: String? ,
    var taskDescription: String? ,
    var taskDate: LocalDate?
)
