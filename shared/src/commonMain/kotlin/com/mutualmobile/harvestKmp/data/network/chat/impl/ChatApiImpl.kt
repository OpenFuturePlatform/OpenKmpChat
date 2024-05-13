package com.mutualmobile.harvestKmp.data.network.chat.impl

import com.mutualmobile.harvestKmp.data.network.Endpoint
import com.mutualmobile.harvestKmp.data.network.chat.ChatApi
import com.mutualmobile.harvestKmp.data.network.getSafeNetworkResponse
import com.mutualmobile.harvestKmp.domain.model.DisplayChatRoom
import com.mutualmobile.harvestKmp.domain.model.request.ChatMessageRequest
import com.mutualmobile.harvestKmp.domain.model.request.GroupChatMessageRequest
import com.mutualmobile.harvestKmp.domain.model.request.GroupCreateRequest
import com.mutualmobile.harvestKmp.domain.model.response.*
import com.mutualmobile.harvestKmp.features.NetworkResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
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

    override suspend fun getChats(recipient: String, sender: String): NetworkResponse<List<ChatMessageResponse>> =
        getSafeNetworkResponse {
            httpClient.get(
                urlString = "${Endpoint.SPRING_BOOT_BASE_URL}${Endpoint.LIST_RECIPIENT_CHATS}$recipient/from/$sender"
            ) {
                contentType(ContentType.Application.Json)
            }
        }

    override suspend fun getChatsByUid(chatUid: String, isGroup: Boolean): NetworkResponse<List<ChatMessageResponse>> =
        getSafeNetworkResponse {
            httpClient.get(
                urlString = "${Endpoint.SPRING_BOOT_BASE_URL}${Endpoint.LIST_CHATS}$chatUid?group=$isGroup"
            ) {
                contentType(ContentType.Application.Json)
            }
        }

    override suspend fun getUserHomeChats(username: String): NetworkResponse<List<DisplayChatRoom>>  =
        getSafeNetworkResponse {
            httpClient.get(
                urlString = "${Endpoint.SPRING_BOOT_BASE_URL}${Endpoint.LIST_RECIPIENT_GROUP}$username"
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

    override suspend fun saveAiChat(message: ChatMessageRequest): NetworkResponse<ChatMessageResponse>  =
        getSafeNetworkResponse {
            httpClient.post(
                urlString = "${Endpoint.SPRING_BOOT_BASE_URL}${Endpoint.CHATGPT_URL}"
            ) {
                contentType(ContentType.Application.Json)
                setBody(message)
            }
        }

    override suspend fun saveGroupChat(message: GroupChatMessageRequest): NetworkResponse<GroupChatMessageResponse> =
        getSafeNetworkResponse {
            httpClient.post(
                urlString = "${Endpoint.SPRING_BOOT_BASE_URL}${Endpoint.CHAT_URL}/group"
            ) {
                contentType(ContentType.Application.Json)
                setBody(message)
            }
        }

    override suspend fun createGroup(request: GroupCreateRequest): NetworkResponse<GroupCreateResponse> =
        getSafeNetworkResponse {
            httpClient.post(
                urlString = "${Endpoint.SPRING_BOOT_BASE_URL}${Endpoint.GROUP_URL}"
            ) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }

    override suspend fun getGroup(groupId: String): NetworkResponse<GroupDetailResponse> =
        getSafeNetworkResponse {
            httpClient.get(
                urlString = "${Endpoint.SPRING_BOOT_BASE_URL}${Endpoint.GROUP_URL}/$groupId"
            ) {
                contentType(ContentType.Application.Json)
            }
        }
}