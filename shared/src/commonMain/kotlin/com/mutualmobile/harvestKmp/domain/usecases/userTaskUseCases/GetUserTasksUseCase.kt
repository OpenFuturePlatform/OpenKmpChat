package com.mutualmobile.harvestKmp.domain.usecases.userTaskUseCases

import com.mutualmobile.harvestKmp.data.network.chat.TaskApi
import com.mutualmobile.harvestKmp.domain.model.response.TaskResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse

class GetUserTasksUseCase(private val taskApi: TaskApi) {
    suspend operator fun invoke(username: String): NetworkResponse<List<TaskResponse>> {
        return taskApi.get(username)
    }
}