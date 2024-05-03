package com.mutualmobile.harvestKmp.domain.usecases.chatApiUseCases

import com.mutualmobile.harvestKmp.data.network.chat.ChatApi
import com.mutualmobile.harvestKmp.domain.model.response.ChatMessageResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse

class GetOwnMessagesUseCase(private val chatApi: ChatApi) {
    suspend operator fun invoke(username: String): NetworkResponse<List<ChatMessageResponse>> {
        return chatApi.getUserChats(username)
    }
}