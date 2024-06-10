package com.mutualmobile.harvestKmp.domain.usecases.chatApiUseCases

import com.mutualmobile.harvestKmp.data.network.chat.ChatApi
import com.mutualmobile.harvestKmp.domain.model.Message
import com.mutualmobile.harvestKmp.domain.model.request.ChatMessageRequest
import com.mutualmobile.harvestKmp.domain.model.response.ChatMessageResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse

class CreateMessagesUseCase(private val chatApi: ChatApi) {
    suspend operator fun invoke(message: Message): NetworkResponse<ChatMessageResponse> {
        return chatApi.saveChat(
            ChatMessageRequest(
                sender = message.user.email,
                recipient = message.recipient,
                body = message.text,
                contentType = message.type,
                attachments = message.attachmentIds
            )
        )
    }
}