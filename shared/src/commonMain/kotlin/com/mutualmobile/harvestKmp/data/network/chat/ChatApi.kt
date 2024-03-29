package com.mutualmobile.harvestKmp.data.network.chat

import com.mutualmobile.harvestKmp.domain.model.request.ChatMessageRequest
import com.mutualmobile.harvestKmp.domain.model.response.ChatMessageResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse

interface ChatApi {
    suspend fun getUserChats(username: String): NetworkResponse<List<ChatMessageResponse>>
    suspend fun saveChat(message: ChatMessageRequest): NetworkResponse<ChatMessageResponse>
}