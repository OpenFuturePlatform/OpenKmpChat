package com.mutualmobile.harvestKmp.features.datamodels.chatApiDataModels

import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes.Screen.withDetail
import com.mutualmobile.harvestKmp.datamodel.ModalPraxisCommand
import com.mutualmobile.harvestKmp.datamodel.NavigationPraxisCommand
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel
import com.mutualmobile.harvestKmp.di.ChatApiUseCaseComponent
import com.mutualmobile.harvestKmp.di.GroupApiUseCaseComponent
import com.mutualmobile.harvestKmp.di.SharedComponent
import com.mutualmobile.harvestKmp.di.UserApiUseCaseComponent
import com.mutualmobile.harvestKmp.domain.model.*
import com.mutualmobile.harvestKmp.domain.model.request.AssistantRequest
import com.mutualmobile.harvestKmp.domain.model.request.GetAssistantRequest
import com.mutualmobile.harvestKmp.features.NetworkResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import org.koin.core.component.KoinComponent
import kotlin.time.Duration

class ChatPrivateDataModel : PraxisDataModel(), KoinComponent {
    private val _dataFlow = MutableSharedFlow<DataState>()
    val dataFlow = _dataFlow.asSharedFlow()

    var currentLoadingJob: Job? = null
    private val chatLocal = SharedComponent().provideChatLocal()

    val chatUid = MutableStateFlow("")
    val isGroup = MutableStateFlow(false)
    val recipient = MutableStateFlow("")
    val sender = MutableStateFlow("")

    private val chatApiUseCaseComponent = ChatApiUseCaseComponent()

    private val createPrivateMessagesUseCase = chatApiUseCaseComponent.provideCreateMessages()
    private val createGroupMessagesUseCase = chatApiUseCaseComponent.provideCreateGroupMessages()
    private val getPrivateMessagesByRecipientUseCase = chatApiUseCaseComponent.providePrivateMessagesByRecipient()
    private val getMessagesByUidUseCase = chatApiUseCaseComponent.provideGetMessagesByUid()
    private val createAssistantNotesUseCase = chatApiUseCaseComponent.provideCreateAssistantNotes()
    private val getAssistantNotesUseCase = chatApiUseCaseComponent.provideGetAssistantNotes()
    private val createAssistantRemindersUseCase = chatApiUseCaseComponent.provideCreateAssistantReminders()
    private val getAssistantRemindersUseCase = chatApiUseCaseComponent.provideGetAssistantReminders()
    private val createAssistantTodosUseCase = chatApiUseCaseComponent.provideCreateAssistantTodos()
    private val getAssistantToDosUseCase = chatApiUseCaseComponent.provideGetAssistantToDos()

    private val uploadAttachmentUseCase = chatApiUseCaseComponent.provideUploadAttachment()
    private val dowloadAttachmentUseCase = chatApiUseCaseComponent.provideDownloadAttachment()

    private val groupApiUseCaseComponent = GroupApiUseCaseComponent()
    private val getGroupUseCase = groupApiUseCaseComponent.provideGetGroup()
    private val userApiUseCaseComponent = UserApiUseCaseComponent()
    private val getUserDetailUseCase = userApiUseCaseComponent.provideGetUserDetail()

    private val attachmentLocal = SharedComponent().provideAttachmentLocal()

    override fun activate() {
        println("LOCAL STORAGE PRIVATE ACTIVATE")
    }

    override fun destroy() {
        dataModelScope.cancel()
    }

    override fun refresh() {
        TODO("Not yet implemented")
    }

    fun getChat(_chatUid: String?, _isGroup: Boolean?, _recipient: String?, _sender: String?) {
        currentLoadingJob?.cancel()

        recipient.value = _recipient!!
        sender.value = _sender!!
        chatUid.value = _chatUid!!
        isGroup.value = _isGroup!!

        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)
            println("GET CHATS IN ACTIVATE with recipient: $_recipient and sender : $_sender")
            when (val response = getMessagesByUidUseCase(chatUid = _chatUid, isGroup = _isGroup)) {
                is NetworkResponse.Success -> {
                    println("PRIVATE CHATS: ${response.data}")

                    _dataFlow.emit(
                        SuccessState(response.data.map {
                            Message(
                                ChatUser(it.id, it.sender, it.sender, ColorProvider.getColor(), null),
                                it.recipient,
                                it.content,
                                it.contentType,
                                true,
                                it.attachments,
                                it.receivedAt.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
                                it.id.toLong()
                            )
                        }.toList())
                    )

                }

                is NetworkResponse.Failure -> {
                    _dataFlow.emit(ErrorState(response.throwable))
                    intPraxisCommand.emit(
                        ModalPraxisCommand(
                            "Failed",
                            response.throwable.message ?: "Failed to find chats"
                        )
                    )
                }

                is NetworkResponse.Unauthorized -> {
                    settings.clear()
                    intPraxisCommand.emit(ModalPraxisCommand("Unauthorized", "Please login again!"))
                    intPraxisCommand.emit(NavigationPraxisCommand(HarvestRoutes.Screen.LOGIN))
                }
            }
        }
    }

    fun getChat(_recipient: String?, _sender: String?) {
        currentLoadingJob?.cancel()
        recipient.value = _recipient!!
        sender.value = _sender!!
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)
            println("GET CHATS IN ACTIVATE with recipient: $_recipient and sender : $_sender")
            when (val response = getPrivateMessagesByRecipientUseCase(receiver = _recipient, sender = _sender)) {
                is NetworkResponse.Success -> {
                    println("PRIVATE CHATS BY RECIPIENT AND SENDER: ${response.data}")

                    _dataFlow.emit(
                        SuccessState(response.data.map {
                            Message(
                                ChatUser(it.id, it.sender, it.sender, ColorProvider.getColor(), null),
                                it.recipient,
                                it.content,
                                it.contentType,
                                true,
                                it.attachments,
                                it.receivedAt.nanosecond.toLong(),
                                it.id.toLong()
                            )
                        }.toList())
                    )

                }

                is NetworkResponse.Failure -> {
                    _dataFlow.emit(ErrorState(response.throwable))
                    intPraxisCommand.emit(
                        ModalPraxisCommand(
                            "Failed",
                            response.throwable.message ?: "Failed to find chats"
                        )
                    )
                }

                is NetworkResponse.Unauthorized -> {
                    settings.clear()
                    intPraxisCommand.emit(ModalPraxisCommand("Unauthorized", "Please login again!"))
                    intPraxisCommand.emit(NavigationPraxisCommand(HarvestRoutes.Screen.LOGIN))
                }
            }
        }
    }

    fun savePrivateChat(message: Message) {
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)
            println("Save Private Chat: $message")
            chatLocal.saveChat(message)

            when (val response = createPrivateMessagesUseCase(message = message)) {
                is NetworkResponse.Success -> {
                    println("Private chat saved successfully: ${response.data}")
                    message.isSent = true
                    val res = mutableListOf(message)
                    _dataFlow.emit(SuccessState(res))
                }

                is NetworkResponse.Failure -> {
                    _dataFlow.emit(ErrorState(response.throwable))
                    intPraxisCommand.emit(
                        ModalPraxisCommand(
                            "Failed",
                            response.throwable.message ?: "Failed to find chats"
                        )
                    )
                }

                is NetworkResponse.Unauthorized -> {
                    settings.clear()
                    intPraxisCommand.emit(ModalPraxisCommand("Unauthorized", "Please login again!"))
                    intPraxisCommand.emit(NavigationPraxisCommand(HarvestRoutes.Screen.LOGIN))
                }
            }

            _dataFlow.emit(Complete)
        }
    }

    fun saveGroupChat(message: Message) {
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)

            when (val response = createGroupMessagesUseCase(message = message)) {
                is NetworkResponse.Success -> {
                    val res = mutableListOf(message)
                    _dataFlow.emit(SuccessState(res))
                }

                is NetworkResponse.Failure -> {
                    _dataFlow.emit(ErrorState(response.throwable))
                    intPraxisCommand.emit(
                        ModalPraxisCommand(
                            "Failed",
                            response.throwable.message ?: "Failed to find chats"
                        )
                    )
                }

                is NetworkResponse.Unauthorized -> {
                    settings.clear()
                    intPraxisCommand.emit(ModalPraxisCommand("Unauthorized", "Please login again!"))
                    intPraxisCommand.emit(NavigationPraxisCommand(HarvestRoutes.Screen.LOGIN))
                }
            }

            _dataFlow.emit(Complete)
        }
    }

    fun saveAssistantNotes(chatId: String, isGroup: Boolean, startDate: String, endDate: String){
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)


            val startTime: LocalDateTime = LocalDate.parse(startDate).atTime(0,0,0) //LocalDateTime.now()
            val endTime: LocalDateTime = LocalDate.parse(endDate).atTime(0,0,0) //datetime.minus(24, DateTimeUnit.HOUR)

            when (val response = createAssistantNotesUseCase(message = AssistantRequest(chatId, isGroup, startTime, endTime))) {
                is NetworkResponse.Success -> {
                    println("AssistantNotes saved successfully: ${response.data}")
                    val res = mutableListOf(response.data)
                    _dataFlow.emit(AssistantSuccessState(res))
                }

                is NetworkResponse.Failure -> {
                    _dataFlow.emit(ErrorState(response.throwable))
                    intPraxisCommand.emit(
                        ModalPraxisCommand(
                            "Failed",
                            response.throwable.message ?: "Failed to find chats"
                        )
                    )
                }

                is NetworkResponse.Unauthorized -> {
                    settings.clear()
                    intPraxisCommand.emit(ModalPraxisCommand("Unauthorized", "Please login again!"))
                    intPraxisCommand.emit(NavigationPraxisCommand(HarvestRoutes.Screen.LOGIN))
                }
            }

            _dataFlow.emit(Complete)
        }
    }

    fun getAssistantNotes(chatId: String, isGroup: Boolean){
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)

            when (val response = getAssistantNotesUseCase(message = GetAssistantRequest(chatId, isGroup))) {
                is NetworkResponse.Success -> {
                    println("AssistantNotes get successfully: ${response.data}")
                    val res = response.data
                    _dataFlow.emit(AssistantSuccessState(res))
                }

                is NetworkResponse.Failure -> {
                    _dataFlow.emit(ErrorState(response.throwable))
                    intPraxisCommand.emit(
                        ModalPraxisCommand(
                            "Failed",
                            response.throwable.message ?: "Failed to find chats"
                        )
                    )
                }

                is NetworkResponse.Unauthorized -> {
                    settings.clear()
                    intPraxisCommand.emit(ModalPraxisCommand("Unauthorized", "Please login again!"))
                    intPraxisCommand.emit(NavigationPraxisCommand(HarvestRoutes.Screen.LOGIN))
                }
            }

            _dataFlow.emit(Complete)
        }
    }

    fun saveAssistantReminders(chatId: String, isGroup: Boolean, startDate: String, endDate: String){
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)

            val startTime: LocalDateTime = LocalDate.parse(startDate).atTime(0,0,0) //LocalDateTime.now()
            val endTime: LocalDateTime = LocalDate.parse(endDate).atTime(0,0,0) //datetime.minus(24, DateTimeUnit.HOUR)

            when (val response = createAssistantRemindersUseCase(message = AssistantRequest(chatId, isGroup, startTime, endTime))) {
                is NetworkResponse.Success -> {
                    println("AssistantReminders saved successfully: ${response.data}")
                    val res = mutableListOf(response.data)
                    _dataFlow.emit(AssistantReminderSuccessState(res))
                }

                is NetworkResponse.Failure -> {
                    _dataFlow.emit(ErrorState(response.throwable))
                    intPraxisCommand.emit(
                        ModalPraxisCommand(
                            "Failed",
                            response.throwable.message ?: "Failed to find chats"
                        )
                    )
                }

                is NetworkResponse.Unauthorized -> {
                    settings.clear()
                    intPraxisCommand.emit(ModalPraxisCommand("Unauthorized", "Please login again!"))
                    intPraxisCommand.emit(NavigationPraxisCommand(HarvestRoutes.Screen.LOGIN))
                }
            }

            _dataFlow.emit(Complete)
        }
    }

    fun getAssistantReminders(chatId: String, isGroup: Boolean){
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)

            when (val response = getAssistantRemindersUseCase(message = GetAssistantRequest(chatId, isGroup))) {
                is NetworkResponse.Success -> {
                    println("AssistantReminders get successfully: ${response.data}")
                    val res = response.data
                    _dataFlow.emit(AssistantReminderSuccessState(res))
                }

                is NetworkResponse.Failure -> {
                    _dataFlow.emit(ErrorState(response.throwable))
                    intPraxisCommand.emit(
                        ModalPraxisCommand(
                            "Failed",
                            response.throwable.message ?: "Failed to find chats"
                        )
                    )
                }

                is NetworkResponse.Unauthorized -> {
                    settings.clear()
                    intPraxisCommand.emit(ModalPraxisCommand("Unauthorized", "Please login again!"))
                    intPraxisCommand.emit(NavigationPraxisCommand(HarvestRoutes.Screen.LOGIN))
                }
            }

            _dataFlow.emit(Complete)
        }
    }

    fun saveAssistantTodos(chatId: String, isGroup: Boolean, startDate: String, endDate: String){
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)

            val startTime: LocalDateTime = LocalDate.parse(startDate).atTime(0,0,0) //LocalDateTime.now()
            val endTime: LocalDateTime = LocalDate.parse(endDate).atTime(0,0,0) //datetime.minus(24, DateTimeUnit.HOUR)

            when (val response = createAssistantTodosUseCase(message = AssistantRequest(chatId, isGroup, startTime, endTime))) {
                is NetworkResponse.Success -> {
                    println("AssistantTodos saved successfully: ${response.data}")
                    val res = mutableListOf(response.data)
                    _dataFlow.emit(AssistantTodoSuccessState(res))
                }

                is NetworkResponse.Failure -> {
                    _dataFlow.emit(ErrorState(response.throwable))
                    intPraxisCommand.emit(
                        ModalPraxisCommand(
                            "Failed",
                            response.throwable.message ?: "Failed to find chats"
                        )
                    )
                }

                is NetworkResponse.Unauthorized -> {
                    settings.clear()
                    intPraxisCommand.emit(ModalPraxisCommand("Unauthorized", "Please login again!"))
                    intPraxisCommand.emit(NavigationPraxisCommand(HarvestRoutes.Screen.LOGIN))
                }
            }

            _dataFlow.emit(Complete)
        }
    }

    fun getAssistantTodos(chatId: String, isGroup: Boolean){
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)

            when (val response = getAssistantToDosUseCase(message = GetAssistantRequest(chatId, isGroup))) {
                is NetworkResponse.Success -> {
                    println("AssistantToDos get successfully: ${response.data}")
                    val res = response.data
                    _dataFlow.emit(AssistantTodoSuccessState(res))
                }

                is NetworkResponse.Failure -> {
                    _dataFlow.emit(ErrorState(response.throwable))
                    intPraxisCommand.emit(
                        ModalPraxisCommand(
                            "Failed",
                            response.throwable.message ?: "Failed to find chats"
                        )
                    )
                }

                is NetworkResponse.Unauthorized -> {
                    settings.clear()
                    intPraxisCommand.emit(ModalPraxisCommand("Unauthorized", "Please login again!"))
                    intPraxisCommand.emit(NavigationPraxisCommand(HarvestRoutes.Screen.LOGIN))
                }
            }

            _dataFlow.emit(Complete)
        }
    }

    fun saveAttachment(
        imageBytes: ByteArray,
        imageCheckSum: String,
        fileName: String,
        sender: ChatUser,
        recipient: String,
        isGroup: Boolean,
        captionText: String
    ) {
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)
            _dataFlow.emit(UploadLoadingState)
            println("Hash attachment: $imageCheckSum")
            val openAttachment = attachmentLocal.findByHash(imageCheckSum)
            if (openAttachment != null) {
                println("Fire local stored attachment details : $openAttachment")
                val message = Message(
                    sender,
                    recipient = recipient,
                    attachmentIds = listOf(openAttachment.attachmentUrl!!.toInt()),
                    text = openAttachment.attachmentUrl,//captionText,
                    type = TextType.ATTACHMENT
                )
                _dataFlow.emit(UploadCompleteState)
                if (isGroup)
                    saveGroupChat(message)
                else
                    savePrivateChat(message)

            } else {
                when (val response = uploadAttachmentUseCase(imageBytes, fileName, captionText)) {
                    is NetworkResponse.Success -> {
                        println("Upload response : ${response.data}")
                        _dataFlow.emit(UploadCompleteState)

                        val message = Message(
                            sender,
                            recipient = recipient,
                            attachmentIds = listOf(response.data),
                            text = captionText,
                            type = TextType.ATTACHMENT
                        )
                        // SAVE FILE ON LOCAL STORAGE
                        attachmentLocal.saveAttachment(
                            Attachment(
                                fileCheckSum = imageCheckSum,
                                fileByteArray = imageBytes,
                                fileType = "",
                                captionText = captionText,
                                isSent = true,
                                attachmentUrl = response.data.toString(),
                                fileName = fileName
                            )
                        )

                        if (isGroup)
                            saveGroupChat(message)
                        else
                            savePrivateChat(message)
                    }

                    is NetworkResponse.Failure -> {
                        _dataFlow.emit(ErrorState(response.throwable))
                        intPraxisCommand.emit(
                            ModalPraxisCommand(
                                "Failed",
                                response.throwable.message ?: "Failed to find chats"
                            )
                        )
                    }

                    is NetworkResponse.Unauthorized -> {
                        settings.clear()
                        intPraxisCommand.emit(ModalPraxisCommand("Unauthorized", "Please login again!"))
                        intPraxisCommand.emit(NavigationPraxisCommand(HarvestRoutes.Screen.LOGIN))
                    }
                }
            }

        }
    }

    suspend fun downloadAttachment(id: Int) : ByteArray {
        return when (val response =
                dowloadAttachmentUseCase(
                   id = id
                )) {
                is NetworkResponse.Success -> {
                    response.data
                }

            is NetworkResponse.Failure -> TODO()
            is NetworkResponse.Unauthorized -> TODO()
        }
    }

    // NAVIGATE TO CONTACT PROFILE FROM PRIVATE SCREEN
    fun navigateContactDetail(contactId: String, isGroup: Boolean) {

        dataModelScope.launch {
            _dataFlow.emit(LoadingState)

            if (isGroup) {

                when (val response = getGroupUseCase(groupId = contactId)) {
                    is NetworkResponse.Success -> {

                        val profileScreen =
                            HarvestRoutes.Screen.GROUP_PROFILE.withDetail(profileId = contactId, isGroup = isGroup)

                        println("Group detail response: ${response.data} for $contactId and $isGroup with screen: $profileScreen")

                        intPraxisCommand.emit(
                            NavigationPraxisCommand(
                                screen = profileScreen
                            )
                        )

                    }

                    is NetworkResponse.Failure -> {
                        _dataFlow.emit(ErrorState(response.throwable))
                        intPraxisCommand.emit(
                            ModalPraxisCommand(
                                "Failed",
                                response.throwable.message ?: "Failed to find chats"
                            )
                        )
                    }

                    is NetworkResponse.Unauthorized -> {
                        settings.clear()
                        intPraxisCommand.emit(ModalPraxisCommand("Unauthorized", "Please login again!"))
                        intPraxisCommand.emit(NavigationPraxisCommand(HarvestRoutes.Screen.LOGIN))
                    }
                }

            } else {

                when (val response = getUserDetailUseCase(email = contactId)) {
                    is NetworkResponse.Success -> {

                        val profileScreen =
                            HarvestRoutes.Screen.CONTACT_PROFILE.withDetail(profileId = contactId, isGroup = isGroup)

                        println("User detail response: ${response.data} for $contactId and $isGroup with screen: $profileScreen")

                        intPraxisCommand.emit(
                            NavigationPraxisCommand(
                                screen = profileScreen
                            )
                        )

                    }

                    is NetworkResponse.Failure -> {
                        _dataFlow.emit(ErrorState(response.throwable))
                        intPraxisCommand.emit(
                            ModalPraxisCommand(
                                "Failed",
                                response.throwable.message ?: "Failed to find chats"
                            )
                        )
                    }

                    is NetworkResponse.Unauthorized -> {
                        settings.clear()
                        intPraxisCommand.emit(ModalPraxisCommand("Unauthorized", "Please login again!"))
                        intPraxisCommand.emit(NavigationPraxisCommand(HarvestRoutes.Screen.LOGIN))
                    }
                }
            }

        }
    }
}