package com.mutualmobile.harvestKmp.domain.usecases.chatApiUseCases

import com.mutualmobile.harvestKmp.data.network.chat.ChatApi
import com.mutualmobile.harvestKmp.domain.model.request.AssistantRequest
import com.mutualmobile.harvestKmp.domain.model.request.GetAssistantRequest
import com.mutualmobile.harvestKmp.domain.model.response.AssistantNotesResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse

class GetAssistantNotesUseCase(private val chatApi: ChatApi) {
    suspend operator fun invoke(message: GetAssistantRequest): NetworkResponse<List<AssistantNotesResponse>> {
        return chatApi.getAssistantNotes(message)
    }
}