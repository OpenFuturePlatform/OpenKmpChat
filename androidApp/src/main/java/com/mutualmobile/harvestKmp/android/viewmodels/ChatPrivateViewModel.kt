package com.mutualmobile.harvestKmp.android.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mutualmobile.harvestKmp.data.network.chat.RealtimeMessagingClient
import com.mutualmobile.harvestKmp.datamodel.PraxisCommand
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel
import com.mutualmobile.harvestKmp.domain.model.Message
import com.mutualmobile.harvestKmp.domain.model.request.User
import com.mutualmobile.harvestKmp.features.datamodels.chatApiDataModels.ChatPrivateDataModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ChatPrivateViewModel(
    client: RealtimeMessagingClient
) : ViewModel() {
    var currentNavigationCommand: PraxisCommand? by mutableStateOf(null)
    var currentPrivateChatState: PraxisDataModel.DataState by mutableStateOf(PraxisDataModel.EmptyState)

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    var canSendMessage: Boolean by mutableStateOf(true)
    private val recipient = MutableStateFlow("")
    private val sender = MutableStateFlow("")
    private val chatUid = MutableStateFlow("")
    private val isGroup = MutableStateFlow(false)

    /**
     * The [User] in the chat room who is currently logged in, in other words the one using the app.
     */
    private val _localUser = MutableStateFlow(User())
    val localUser: StateFlow<User> = _localUser

    var chats by mutableStateOf(emptyList<Message>())

    private val getChatPrivateDataModel = ChatPrivateDataModel()

    init {
        println("ChatPrivateViewModel call init")
        getCurrentUserChats()
        with(getChatPrivateDataModel) {
            observeDataState()
            observeNavigationCommands()
        }
    }

    private fun ChatPrivateDataModel.observeNavigationCommands() =
        praxisCommand.onEach { newCommand ->
            currentNavigationCommand = newCommand
        }.launchIn(viewModelScope)

    private fun ChatPrivateDataModel.observeDataState() {
        dataFlow.onEach { newState ->
            currentPrivateChatState = newState
        }.launchIn(viewModelScope)
    }

    private fun getCurrentUserChats() {
        _loading.value = true
        with(getChatPrivateDataModel) {
            dataFlow.onEach { newChatState ->
                println("PRIVATE CHAT STATE $newChatState")
                if (newChatState is PraxisDataModel.SuccessState<*>) {
                    val newMessage = newChatState.data as List<Message>

                    chats = if (newMessage.size > 1)
                        newMessage
                    else
                        chats.plus(newMessage)
                }

                else if (newChatState is PraxisDataModel.UploadLoadingState){
                    canSendMessage = false
                }

                else if (newChatState is PraxisDataModel.UploadCompleteState){
                    canSendMessage = true
                }
            }.launchIn(viewModelScope)
            activate()
        }
        _loading.value = false
    }

    fun savePrivateChat(message: Message) {
        getChatPrivateDataModel.savePrivateChat(message)
    }

    fun saveGroupChat(message: Message) {
        getChatPrivateDataModel.saveGroupChat(message)
    }

    fun uploadAttachment(
        imageBytes: ByteArray,
        imageCheckSum: String,
        fileName: String,
        message: Message,
        isGroup: Boolean
    ) {
        getChatPrivateDataModel.saveAttachment(
            imageBytes = imageBytes,
            imageCheckSum = imageCheckSum,
            fileName = fileName,
            sender = message.user,
            recipient = message.recipient,
            isGroup = isGroup,
            captionText = message.text
        )
    }

    suspend fun downloadAttachment(
        id: Int
    ) : ByteArray {
        return getChatPrivateDataModel.downloadAttachment(id)
    }

//    fun uploadAttachment(attachment: Attachment, message: Message, isGroup: Boolean) {
//        getChatPrivateDataModel.saveAttachment(attachment = attachment, sender = message.user, recipient = message.recipient, isGroup = isGroup)
//    }

    fun getPrivateChats(_chatUid: String, _isGroup: Boolean, _recipient: String, _sender: String) {
        println("PRIVATE SCREEN CALL CHAT LIST")
        recipient.value = _recipient
        sender.value = _sender
        chatUid.value = _chatUid
        isGroup.value = _isGroup
        chats = emptyList()
        getChatPrivateDataModel.getChat(_chatUid, _isGroup, _recipient, _sender)
    }

    fun getPrivateChats(_recipient: String, _sender: String) {
        println("PRIVATE SCREEN CALL CHAT LIST")
        recipient.value = _recipient
        sender.value = _sender
        chats = emptyList()
        getChatPrivateDataModel.getChat(_recipient, _sender)
    }

    fun onContactProfileClicked(contactId: String, isGroup: Boolean) {
        println("Contact details: $contactId and isGroup: $isGroup")
        getChatPrivateDataModel.navigateContactDetail(contactId, isGroup)
    }

    fun resetAll(onComplete: () -> Unit = {}) {
        currentNavigationCommand = null
        currentPrivateChatState = PraxisDataModel.EmptyState
        onComplete()
    }

}