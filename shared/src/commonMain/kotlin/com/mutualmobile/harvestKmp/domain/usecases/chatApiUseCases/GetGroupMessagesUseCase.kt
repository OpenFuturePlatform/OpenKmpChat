package com.mutualmobile.harvestKmp.domain.usecases.chatApiUseCases

import com.mutualmobile.harvestKmp.data.network.chat.ChatApi
import com.mutualmobile.harvestKmp.domain.model.DisplayChatRoom
import com.mutualmobile.harvestKmp.domain.model.response.UserHomeChatMessageResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse

class GetGroupMessagesUseCase(private val chatApi: ChatApi) {
    suspend operator fun invoke(username: String): NetworkResponse<List<DisplayChatRoom>> {
        return chatApi.getUserHomeChats(username)
    }
}