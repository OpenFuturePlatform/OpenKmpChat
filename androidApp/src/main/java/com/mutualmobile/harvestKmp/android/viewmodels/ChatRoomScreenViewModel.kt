package com.mutualmobile.harvestKmp.android.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mutualmobile.harvestKmp.data.network.TAG
import com.mutualmobile.harvestKmp.datamodel.OpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import com.mutualmobile.harvestKmp.domain.model.DisplayChatRoom
import com.mutualmobile.harvestKmp.domain.model.request.User
import com.mutualmobile.harvestKmp.domain.model.response.GetUserResponse
import com.mutualmobile.harvestKmp.features.datamodels.chatApiDataModels.ChatGroupDataModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ChatRoomScreenViewModel : ViewModel() {
    var currentNavigationCommand: OpenCommand? by mutableStateOf(null)
    var currentHomeChatState: OpenDataModel.DataState by mutableStateOf(OpenDataModel.EmptyState)

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    /**
     * The logged in user who is using the app.
     */
    private val _currentUser = MutableStateFlow(User())
    val currentUser: StateFlow<User> = _currentUser

    /**
     * All [ChatRoom]s which the [currentUser] is participating in, as [DisplayChatRoom] objects.
     * All of these have at least one message.
     */
    private val _chats = MutableStateFlow(listOf<DisplayChatRoom>())
    var chats: StateFlow<List<DisplayChatRoom>> = _chats

    private val getChatGroupDataModel = ChatGroupDataModel()

    init {
        println("ChatRoomViewModel call init")
        getCurrentUserAndChats()
        with(getChatGroupDataModel) {
            observeDataState()
            observeNavigationCommands()
        }
    }

    private fun ChatGroupDataModel.observeNavigationCommands() =
        praxisCommand.onEach { newCommand ->
            println("Chat Group command $newCommand")
            currentNavigationCommand = newCommand
        }.launchIn(viewModelScope)

    private fun ChatGroupDataModel.observeDataState() {
        dataFlow.onEach { newState ->
            currentHomeChatState = newState
        }.launchIn(viewModelScope)
    }

    private fun navigateToChatRoom(chatRoomUid: String, isGroup: Boolean, recipient: String) {
        Log.d(TAG, "Sending new chat room event, with chat room UID $chatRoomUid... with local user ${currentUser.value}")
        getChatGroupDataModel.getUserPrivateChats(chatRoomUid, isGroup, recipient, currentUser.value.email!!)
    }

    private fun getCurrentUserAndChats() {
        _loading.value = true
        with(getChatGroupDataModel) {
            //println("GROUP CHAT STATE")
            dataFlow.onEach { newChatState ->
                if (newChatState is OpenDataModel.SuccessState<*>) {
                    //println("NEW ROOM STATE WITH ${newChatState.data}")
                    val newGroupMessage = newChatState.data as List<DisplayChatRoom>
                    _chats.value = newGroupMessage
                }
            }.launchIn(viewModelScope)
            activate()
        }
        _loading.value = false

    }

    /**
     * Called when a chat room card is clicked.
     * @param position Index of the card in [chats].
     */
    fun onChatClicked(position: Int) {
        val chatUid = chats.value[position].chatUid
        val recipient = chats.value[position].chatRoomName
        val isGroup = chats.value[position].group
        navigateToChatRoom(chatUid, isGroup, recipient)
    }

    fun getUserGroupChats(userState: OpenDataModel.SuccessState<*>){
        val userResponse = userState.data as GetUserResponse
        _currentUser.value = User(id = userResponse.id, firstName = userResponse.firstName, lastName = userResponse.lastName, email = userResponse.email)
        getChatGroupDataModel.getUserGroupChats(username = userResponse.email ?: "")
    }

    fun resetAll(onComplete: () -> Unit = {}) {
        currentNavigationCommand = null
        currentHomeChatState = OpenDataModel.EmptyState
        onComplete()
    }
}