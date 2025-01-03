package com.mutualmobile.harvestKmp.android.viewmodels

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mutualmobile.harvestKmp.data.network.chat.RealtimeMessagingClient
import com.mutualmobile.harvestKmp.datamodel.PraxisCommand
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel
import com.mutualmobile.harvestKmp.db.flattenToList
import com.mutualmobile.harvestKmp.di.SharedComponent
import com.mutualmobile.harvestKmp.domain.model.ChatUser
import com.mutualmobile.harvestKmp.domain.model.DisplayChatRoom
import com.mutualmobile.harvestKmp.domain.model.Message
import com.mutualmobile.harvestKmp.domain.model.request.ChatMessageRequest
import com.mutualmobile.harvestKmp.domain.model.response.GetUserResponse
import com.mutualmobile.harvestKmp.domain.model.response.OrgProjectResponse
import com.mutualmobile.harvestKmp.features.datamodels.authApiDataModels.GetUserDataModel
import com.mutualmobile.harvestKmp.features.datamodels.authApiDataModels.LoginDataModel
import com.mutualmobile.harvestKmp.features.datamodels.chatApiDataModels.ChatDataModel
import db.Harvest_chat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.net.ConnectException

class ChatViewModel(
    client: RealtimeMessagingClient
) : ViewModel() {
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    var chats by mutableStateOf(emptyList<Message>())
    var canSendMessage: Boolean by mutableStateOf(true)

    private val getChatDataModel = ChatDataModel()

    val state = client
        .getStateStream()
        .onStart { _isConnecting.value = true }
        .onEach { _isConnecting.value = false }
        .catch { t ->
            _isConnecting.value = false
            _showConnectionError.value = t is ConnectException
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), String())

    private val _isConnecting = MutableStateFlow(false)
    val isConnecting = _isConnecting.asStateFlow()

    private val _showConnectionError = MutableStateFlow(false)
    val showConnectionError = _showConnectionError.asStateFlow()

    init {
        getCurrentUserChats()
        canSendMessage = true
    }

    private fun getCurrentUserChats() {
        _loading.value = true
        with(getChatDataModel) {
            dataFlow.onEach { newChatState ->
                println("NEW STATE CHAT GPT : $newChatState")
                if (newChatState is PraxisDataModel.SuccessState<*>) {
                    println("NEW STATE CHAT GPT WITH ${newChatState.data}")
                    val newMessage = newChatState.data as List<Message>

                    if (newMessage.isEmpty()){
                        canSendMessage = true
                    }
                    chats = if (newMessage.size > 1)
                        newMessage
                    else
                        chats.plus(newMessage)
                }
                if (newChatState is PraxisDataModel.LoadingState){
                    canSendMessage = false
                }
                if (newChatState is PraxisDataModel.Complete){
                    canSendMessage = true
                }
            }.launchIn(viewModelScope)
            activate()
        }
        _loading.value = false
    }

    fun saveChatGptChat(message: Message) {
        getChatDataModel.saveChatGptChat(message)
    }

    fun getUserChats(userState: PraxisDataModel.SuccessState<*>){
        getChatDataModel.getUserChats(username = (userState.data as GetUserResponse).firstName ?: "")
    }

}