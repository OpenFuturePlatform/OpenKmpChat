package com.mutualmobile.harvestKmp.domain.usecases.chatApiUseCases

import com.mutualmobile.harvestKmp.data.network.chat.ChatApi
import com.mutualmobile.harvestKmp.domain.model.Message
import com.mutualmobile.harvestKmp.domain.model.request.GroupChatMessageRequest
import com.mutualmobile.harvestKmp.domain.model.response.GroupChatMessageResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse

class CreateGroupMessagesUseCase(private val chatApi: ChatApi) {
    suspend operator fun invoke(message: Message): NetworkResponse<GroupChatMessageResponse> {
        return chatApi.saveGroupChat(GroupChatMessageRequest(
            sender = message.user.email,
            groupId = message.recipient,
            body = message.text,
            contentType = message.type))
    }
}