package com.mutualmobile.harvestKmp.data.network.chat

import com.mutualmobile.harvestKmp.domain.model.DisplayChatRoom
import com.mutualmobile.harvestKmp.domain.model.request.ChatMessageRequest
import com.mutualmobile.harvestKmp.domain.model.request.GroupChatMessageRequest
import com.mutualmobile.harvestKmp.domain.model.request.GroupCreateRequest
import com.mutualmobile.harvestKmp.domain.model.response.*
import com.mutualmobile.harvestKmp.features.NetworkResponse

interface ChatApi {
    suspend fun getUserChats(username: String): NetworkResponse<List<ChatMessageResponse>>
    suspend fun getChats(recipient: String, sender: String): NetworkResponse<List<ChatMessageResponse>>
    suspend fun getChatsByUid(chatUid: String, isGroup: Boolean): NetworkResponse<List<ChatMessageResponse>>
    suspend fun getUserHomeChats(username: String): NetworkResponse<List<DisplayChatRoom>>
    suspend fun saveChat(message: ChatMessageRequest): NetworkResponse<ChatMessageResponse>
    suspend fun saveAiChat(message: ChatMessageRequest): NetworkResponse<ChatMessageResponse>
    suspend fun saveGroupChat(message: GroupChatMessageRequest): NetworkResponse<GroupChatMessageResponse>
    suspend fun createGroup(request: GroupCreateRequest): NetworkResponse<GroupCreateResponse>
    suspend fun getGroup(groupId: String): NetworkResponse<GroupDetailResponse>
}