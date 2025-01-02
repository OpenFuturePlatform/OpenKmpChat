package com.mutualmobile.harvestKmp.android.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mutualmobile.harvestKmp.datamodel.OpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import com.mutualmobile.harvestKmp.domain.model.Message
import com.mutualmobile.harvestKmp.domain.model.request.User
import com.mutualmobile.harvestKmp.domain.model.response.AssistantNotesResponse
import com.mutualmobile.harvestKmp.domain.model.response.AssistantReminderResponse
import com.mutualmobile.harvestKmp.domain.model.response.AssistantTodosResponse
import com.mutualmobile.harvestKmp.features.datamodels.chatApiDataModels.ChatPrivateDataModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ChatPrivateScreenViewModel : ViewModel() {
    var currentNavigationCommand: OpenCommand? by mutableStateOf(null)
    var currentPrivateChatState: OpenDataModel.DataState by mutableStateOf(OpenDataModel.EmptyState)

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    var canSendMessage: Boolean by mutableStateOf(true)

    var assistantNotesReady: Boolean by mutableStateOf(false)
    var assistantNotes by mutableStateOf(emptyList<AssistantNotesResponse>())
    var assistantReminders by mutableStateOf(emptyList<AssistantReminderResponse>())
    var assistantTodos by mutableStateOf(emptyList<AssistantTodosResponse>())
    var currentAssistantType by mutableStateOf("")

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

    var isAssistantNotesDialogVisible by mutableStateOf(false)
    var isAssistantRemindersDialogVisible by mutableStateOf(false)
    var isAssistantToDosDialogVisible by mutableStateOf(false)

    var assistantConfirmChatId by mutableStateOf("")
    var assistantConfirmIsGroup by mutableStateOf("")
    var assistantStartDate by mutableStateOf("" )
    var assistantEndDate by mutableStateOf("" )


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
                println("CurrentUserChats STATE $newChatState")
                when (newChatState){
                    is OpenDataModel.SuccessState<*> -> {
                        val newMessage = newChatState.data as List<Message>
                        chats = if (newMessage.size > 1)
                            newMessage
                        else
                            chats.plus(newMessage)
                    }

                    is OpenDataModel.UploadLoadingState, OpenDataModel.UploadCompleteState -> {
                        canSendMessage = false
                    }

                    is OpenDataModel.AssistantSuccessState<*> -> {
                        assistantNotes  = newChatState.data as List<AssistantNotesResponse>
                        assistantNotesReady = true
                        currentAssistantType = "NOTES"
                    }
                    is OpenDataModel.AssistantReminderSuccessState<*> -> {
                        assistantReminders  = newChatState.data as List<AssistantReminderResponse>
                        assistantNotesReady = true
                        currentAssistantType = "REMINDERS"
                    }

                    is OpenDataModel.AssistantTodoSuccessState<*> -> {
                        assistantTodos  = newChatState.data as List<AssistantTodosResponse>
                        assistantNotesReady = true
                        currentAssistantType = "TODOS"
                    }

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
        currentPrivateChatState = OpenDataModel.EmptyState
        onComplete()
    }

    fun addAssistantNotes(){
        println("Assistant start Date : $assistantStartDate and end Date : $assistantEndDate")
        if (assistantConfirmChatId != "" && assistantConfirmIsGroup != "") {
            getChatPrivateDataModel.saveAssistantNotes(assistantConfirmChatId, assistantConfirmIsGroup == "true", startDate = assistantStartDate, endDate = assistantEndDate)
        }
    }

    fun getAssistantNotes(
        chatId: String,
        isGroup: Boolean
    ){
        getChatPrivateDataModel.getAssistantNotes(chatId, isGroup)
    }

    fun addAssistantReminders(){
        println("Assistant start Date : $assistantStartDate and end Date : $assistantEndDate")
        if (assistantConfirmChatId != "" && assistantConfirmIsGroup != "") {
            getChatPrivateDataModel.saveAssistantReminders(assistantConfirmChatId, assistantConfirmIsGroup == "true", startDate = assistantStartDate, endDate = assistantEndDate)
        }
    }

    fun getAssistantReminders(
        chatId: String,
        isGroup: Boolean
    ){
        getChatPrivateDataModel.getAssistantReminders(chatId, isGroup)
    }

    fun addAssistantToDos(){
        println("Assistant start Date : $assistantStartDate and end Date : $assistantEndDate")
        if (assistantConfirmChatId != "" && assistantConfirmIsGroup != "") {
            getChatPrivateDataModel.saveAssistantTodos(assistantConfirmChatId, assistantConfirmIsGroup == "true", startDate = assistantStartDate, endDate = assistantEndDate)
        }
    }

    fun getAssistantToDos(
        chatId: String,
        isGroup: Boolean
    ){
        getChatPrivateDataModel.getAssistantTodos(chatId, isGroup)
    }

}