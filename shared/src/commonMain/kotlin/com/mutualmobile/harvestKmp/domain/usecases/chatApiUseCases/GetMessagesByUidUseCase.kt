package com.mutualmobile.harvestKmp.domain.usecases.chatApiUseCases

import com.mutualmobile.harvestKmp.data.network.chat.ChatApi
import com.mutualmobile.harvestKmp.domain.model.response.ChatMessageResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse

class GetMessagesByUidUseCase(private val chatApi: ChatApi) {
    suspend operator fun invoke(chatUid: String, isGroup: Boolean): NetworkResponse<List<ChatMessageResponse>> {
        return chatApi.getChatsByUid(chatUid, isGroup)
    }
}