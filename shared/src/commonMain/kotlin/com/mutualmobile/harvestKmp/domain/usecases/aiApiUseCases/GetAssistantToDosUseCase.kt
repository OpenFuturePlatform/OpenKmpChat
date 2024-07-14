package com.mutualmobile.harvestKmp.domain.usecases.aiApiUseCases

import com.mutualmobile.harvestKmp.data.network.chat.ChatApi
import com.mutualmobile.harvestKmp.domain.model.request.GetAssistantRequest
import com.mutualmobile.harvestKmp.domain.model.response.AssistantTodosResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse

class GetAssistantToDosUseCase(private val chatApi: ChatApi) {
    suspend operator fun invoke(message: GetAssistantRequest): NetworkResponse<List<AssistantTodosResponse>> {
        return chatApi.getAssistantToDos(message)
    }
}