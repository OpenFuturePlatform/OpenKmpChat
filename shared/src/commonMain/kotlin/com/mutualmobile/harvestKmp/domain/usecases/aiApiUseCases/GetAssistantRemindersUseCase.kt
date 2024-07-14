package com.mutualmobile.harvestKmp.domain.usecases.aiApiUseCases

import com.mutualmobile.harvestKmp.data.network.chat.ChatApi
import com.mutualmobile.harvestKmp.domain.model.request.GetAssistantRequest
import com.mutualmobile.harvestKmp.domain.model.response.AssistantReminderResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse

class GetAssistantRemindersUseCase(private val chatApi: ChatApi) {
    suspend operator fun invoke(message: GetAssistantRequest): NetworkResponse<List<AssistantReminderResponse>> {
        return chatApi.getAssistantReminders(message)
    }
}