package com.mutualmobile.harvestKmp.data.network.chat.impl

import com.mutualmobile.harvestKmp.data.network.Endpoint
import com.mutualmobile.harvestKmp.data.network.chat.ChatApi
import com.mutualmobile.harvestKmp.data.network.getSafeNetworkResponse
import com.mutualmobile.harvestKmp.domain.model.request.ChatMessageRequest
import com.mutualmobile.harvestKmp.domain.model.response.ChatMessageResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class ChatApiImpl(
    private val httpClient: HttpClient
) : ChatApi {
    override suspend fun getUserChats(username: String): NetworkResponse<List<ChatMessageResponse>> =
        getSafeNetworkResponse {
            httpClient.get(
                urlString = "${Endpoint.SPRING_BOOT_BASE_URL}${Endpoint.LIST_RECIPIENT_CHATS}$username"
            ) {
                contentType(ContentType.Application.Json)
            }
        }

    override suspend fun saveChat(message: ChatMessageRequest): NetworkResponse<ChatMessageResponse> =
        getSafeNetworkResponse {
            httpClient.post(
                urlString = "${Endpoint.SPRING_BOOT_BASE_URL}${Endpoint.CHAT_URL}"
            ) {
                contentType(ContentType.Application.Json)
                setBody(message)
            }
        }
}