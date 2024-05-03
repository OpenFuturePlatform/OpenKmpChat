package com.mutualmobile.harvestKmp.domain.usecases.chatApiUseCases

import com.mutualmobile.harvestKmp.data.network.chat.ChatApi
import com.mutualmobile.harvestKmp.domain.model.response.ChatMessageResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse

class GetPrivateMessagesUseCase(private val chatApi: ChatApi) {
    suspend operator fun invoke(receiver: String, sender: String): NetworkResponse<List<ChatMessageResponse>> {
        return chatApi.getChats(receiver, sender)
    }
}