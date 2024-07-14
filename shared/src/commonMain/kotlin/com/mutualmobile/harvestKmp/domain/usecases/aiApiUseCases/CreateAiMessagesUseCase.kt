package com.mutualmobile.harvestKmp.domain.usecases.aiApiUseCases

import com.mutualmobile.harvestKmp.data.network.chat.ChatApi
import com.mutualmobile.harvestKmp.domain.model.AiMessage
import com.mutualmobile.harvestKmp.domain.model.Message
import com.mutualmobile.harvestKmp.domain.model.request.ChatMessageRequest
import com.mutualmobile.harvestKmp.domain.model.response.ChatMessageResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse

class CreateAiMessagesUseCase(private val chatApi: ChatApi) {
    suspend operator fun invoke(message: AiMessage): NetworkResponse<ChatMessageResponse> {
        return chatApi.saveAiChat(
            ChatMessageRequest(
                sender = message.sender,
                recipient = null,
                body = message.body,
                contentType = message.contentType,
                attachments = emptyList()
            )
        )
    }
}