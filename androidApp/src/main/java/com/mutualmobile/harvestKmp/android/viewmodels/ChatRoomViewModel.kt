package com.mutualmobile.harvestKmp.android.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mutualmobile.harvestKmp.data.network.TAG
import com.mutualmobile.harvestKmp.datamodel.PraxisCommand
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel
import com.mutualmobile.harvestKmp.domain.model.DisplayChatRoom
import com.mutualmobile.harvestKmp.domain.model.response.GetUserResponse
import com.mutualmobile.harvestKmp.features.datamodels.authApiDataModels.LoginDataModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.Instant
import java.time.LocalDateTime
import java.util.Date

class ChatRoomViewModel : ViewModel() {
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    /**
     * All [ChatRoom]s which the [currentUser] is participating in, as [DisplayChatRoom] objects.
     * All of these have at least one message.
     */
    private val _chats = MutableStateFlow(listOf<DisplayChatRoom>())
    val chats: StateFlow<List<DisplayChatRoom>> = _chats

    init {
        getCurrentUserAndChats()
    }

    private fun navigateToChatRoom(chatRoomUid: String) {
        Log.d(TAG, "Sending new chat room event, with chat room UID $chatRoomUid...")
    }

    private fun getCurrentUserAndChats() {
        _loading.value = true
        val now: Instant = Clock.System.now()
        val chat1 = DisplayChatRoom(
            chatUid = "1",
            chatRoomName = "SELF CHAT",
            group = false,
            displayUserName = "Beksultan",
            memberCount = 0,
            lastMessageText = "what was about",
            lastMessageTime = now.epochSeconds,
            chatRoomPicture = null //this is not implemented yet
        )
        val chat2 = DisplayChatRoom(
            chatUid = "2",
            chatRoomName = "GROUP CHAT",
            group = false,
            displayUserName = "MAKSIM",
            memberCount = 15,
            lastMessageText = "hello guys",
            lastMessageTime = now.epochSeconds,
            chatRoomPicture = null //this is not implemented yet
        )
        _chats.value = mutableListOf(chat1, chat2)

        _loading.value = false

    }

    /**
     * Called when a chat room card is clicked.
     * @param position Index of the card in [chats].
     */
    fun onChatClicked(position: Int) {
        val chatUid = chats.value[position].chatUid
        navigateToChatRoom(chatUid)
    }
}