package com.mutualmobile.harvestKmp.data.network.chat

import com.mutualmobile.harvestKmp.domain.model.DisplayChatRoom
import com.mutualmobile.harvestKmp.domain.model.request.*
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
    suspend fun saveAssistantNotes(message: AssistantRequest): NetworkResponse<AssistantNotesResponse>
    suspend fun saveAssistantReminders(message: AssistantRequest): NetworkResponse<AssistantReminderResponse>
    suspend fun saveAssistantToDos(message: AssistantRequest): NetworkResponse<AssistantTodosResponse>
    suspend fun getAssistantNotes(message: GetAssistantRequest): NetworkResponse<List<AssistantNotesResponse>>
    suspend fun getAssistantReminders(message: GetAssistantRequest): NetworkResponse<List<AssistantReminderResponse>>
    suspend fun getAssistantToDos(message: GetAssistantRequest): NetworkResponse<List<AssistantTodosResponse>>
}