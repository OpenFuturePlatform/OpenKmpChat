package com.mutualmobile.harvestKmp.data.network.chat

import com.mutualmobile.harvestKmp.domain.model.request.TaskRequest
import com.mutualmobile.harvestKmp.domain.model.response.TaskResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse

interface TaskApi {
    suspend fun get(username: String): NetworkResponse<List<TaskResponse>>
    suspend fun save(taskRequest: TaskRequest): NetworkResponse<TaskResponse>
}