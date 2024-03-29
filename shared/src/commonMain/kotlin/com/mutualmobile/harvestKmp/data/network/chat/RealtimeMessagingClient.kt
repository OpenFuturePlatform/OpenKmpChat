package com.mutualmobile.harvestKmp.data.network.chat

import com.mutualmobile.harvestKmp.domain.model.request.ChatMessageRequest
import kotlinx.coroutines.flow.Flow

interface RealtimeMessagingClient {
    fun getStateStream(): Flow<String>
    suspend fun sendAction(action: ChatMessageRequest)
    suspend fun close()
}