package com.mutualmobile.harvestKmp.features.datamodels.chatApiDataModels

import com.mutualmobile.harvestKmp.data.network.Endpoint
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes.Screen.withDetail
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes.Screen.withRecipient
import com.mutualmobile.harvestKmp.datamodel.ModalPraxisCommand
import com.mutualmobile.harvestKmp.datamodel.NavigationPraxisCommand
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel
import com.mutualmobile.harvestKmp.di.ChatApiUseCaseComponent
import com.mutualmobile.harvestKmp.di.GroupApiUseCaseComponent
import com.mutualmobile.harvestKmp.di.SharedComponent
import com.mutualmobile.harvestKmp.domain.model.*
import com.mutualmobile.harvestKmp.domain.model.response.GetUserResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse
import dev.icerock.moko.graphics.Color
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

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

    private val uploadAttachmentUseCase = chatApiUseCaseComponent.provideUploadAttachment()

    private val groupApiUseCaseComponent = GroupApiUseCaseComponent()
    private val getGroupUseCase = groupApiUseCaseComponent.provideGetGroup()

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
                                it.content,
                                it.contentType,
                                true,
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
                                it.content,
                                it.contentType,
                                true,
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

            chatLocal.saveChat(message)

            when (val response = createPrivateMessagesUseCase(message = message)) {
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
        //fun saveAttachment(attachment: Attachment, sender: ChatUser, recipient: String, isGroup: Boolean) {
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)

            println("Hash attachment: $imageCheckSum")
            val openAttachment = attachmentLocal.findByHash(imageCheckSum)
            if (openAttachment != null) {
                println("Fire local stored attachment details : $openAttachment")
                val message = Message(
                    sender,
                    recipient = recipient,
                    attachmentUrl = openAttachment.attachmentUrl!!,
                    text = openAttachment.attachmentUrl,//captionText,
                    type = TextType.ATTACHMENT
                )

                if (isGroup)
                    saveGroupChat(message)
                else
                    savePrivateChat(message)

            } else {
                when (val response = uploadAttachmentUseCase(imageBytes, fileName, captionText)) {
                    is NetworkResponse.Success -> {
                        println("Upload response ${response.data}")
                        val imageUrl = "${Endpoint.SPRING_BOOT_BASE_URL}${Endpoint.ATTACHMENT_URL}/${response.data}"
                        val message = Message(
                            sender,
                            recipient = recipient,
                            attachmentUrl = imageUrl,
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
                                attachmentUrl = imageUrl,
                                fileName = ""
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

    // NAVIGATE TO CONTACT PROFILE FROM PRIVATE SCREEN
    fun navigateContactDetail(contactId: String, isGroup: Boolean) {

        dataModelScope.launch {
            _dataFlow.emit(LoadingState)

            when (val response = getGroupUseCase(groupId = contactId)) {
                is NetworkResponse.Success -> {

                    val profileScreen =
                        HarvestRoutes.Screen.CONTACT_PROFILE.withDetail(profileId = contactId, isGroup = isGroup)

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

        }
    }
}