package com.mutualmobile.harvestKmp.domain.usecases.userTaskUseCases

import com.mutualmobile.harvestKmp.data.network.chat.TaskApi
import com.mutualmobile.harvestKmp.domain.model.request.TaskRequest
import com.mutualmobile.harvestKmp.domain.model.response.TaskResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse

class SaveUserTasksUseCase(private val taskApi: TaskApi) {
    suspend operator fun invoke(taskRequest: TaskRequest): NetworkResponse<TaskResponse> {
        return taskApi.
        save(taskRequest)
    }
}