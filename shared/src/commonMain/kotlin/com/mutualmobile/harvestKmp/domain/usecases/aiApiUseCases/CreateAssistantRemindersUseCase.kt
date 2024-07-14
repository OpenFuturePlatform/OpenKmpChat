package com.mutualmobile.harvestKmp.domain.usecases.aiApiUseCases

import com.mutualmobile.harvestKmp.data.network.chat.ChatApi
import com.mutualmobile.harvestKmp.domain.model.request.AssistantRequest
import com.mutualmobile.harvestKmp.domain.model.response.AssistantNotesResponse
import com.mutualmobile.harvestKmp.domain.model.response.AssistantReminderResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse

class CreateAssistantRemindersUseCase(private val chatApi: ChatApi) {
    suspend operator fun invoke(message: AssistantRequest): NetworkResponse<AssistantReminderResponse> {
        return chatApi.saveAssistantReminders(message)
    }
}