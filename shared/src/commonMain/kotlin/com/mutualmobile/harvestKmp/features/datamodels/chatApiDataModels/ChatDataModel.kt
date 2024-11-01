package com.mutualmobile.harvestKmp.features.datamodels.chatApiDataModels

import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes
import com.mutualmobile.harvestKmp.datamodel.ModalOpenCommand
import com.mutualmobile.harvestKmp.datamodel.NavigationOpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import com.mutualmobile.harvestKmp.di.ChatApiUseCaseComponent
import com.mutualmobile.harvestKmp.di.SharedComponent
import com.mutualmobile.harvestKmp.domain.model.*
import com.mutualmobile.harvestKmp.features.NetworkResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class ChatDataModel : OpenDataModel(), KoinComponent {

    val _dataFlow = MutableSharedFlow<DataState>()
    val dataFlow = _dataFlow.asSharedFlow()

    var currentLoadingJob: Job? = null
    private val chatLocal = SharedComponent().provideChatLocal()

    private val chatApiUseCaseComponent = ChatApiUseCaseComponent()
    private val getMessagesByRecipientUseCase = chatApiUseCaseComponent.provideGetMessagesByRecipient()
    private val createMessagesUseCase = chatApiUseCaseComponent.provideCreateMessages()
    private val createAiMessagesUseCase = chatApiUseCaseComponent.provideCreateAiMessages()
    override fun activate() {
        println("LOCAL STORAGE ACTIVATE")
        //getChat()
    }

    override fun destroy() {
        dataModelScope.cancel()
    }

    override fun refresh() {
        TODO("Not yet implemented")
    }

    fun saveChatGptChat(message: Message) {
        currentLoadingJob?.cancel()
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)

            val res = mutableListOf(message)
            _dataFlow.emit(SuccessState(res))


            val aiMessage = AiMessage(sender = message.user.email, contentType = message.type, body = message.text)

            when (val response = createAiMessagesUseCase(message = aiMessage)) {
                is NetworkResponse.Success -> {
                    println("AI RESPONSE: ${response.data}")
                    val aiUser =
                        ChatUser(name = "AI_ASSISTANT", id = "", email = "openai@openfuture.io", picture = null)
                    val responseMessage = Message(
                        user = aiUser,
                        recipient = response.data.recipient,
                        text = response.data.content,
                        attachmentIds = emptyList(),
                        type = response.data.contentType
                    )
                    _dataFlow.emit(SuccessState(mutableListOf(responseMessage)))
                    _dataFlow.emit(Complete)
                }

                is NetworkResponse.Failure -> {
                    _dataFlow.emit(ErrorState(response.throwable))
                    intOpenCommand.emit(
                        ModalOpenCommand(
                            "Failed",
                            response.throwable.message ?: "Failed to find chats"
                        )
                    )
                }

                is NetworkResponse.Unauthorized -> {
                    settings.clear()
                    intOpenCommand.emit(ModalOpenCommand("Unauthorized", "Please login again!"))
                    intOpenCommand.emit(NavigationOpenCommand(HarvestRoutes.Screen.LOGIN))
                }
            }

        }
    }

    fun getUserChats(username: String) {
        currentLoadingJob?.cancel()
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)

            when (val response = getMessagesByRecipientUseCase(username = username)) {
                is NetworkResponse.Success -> {
                    _dataFlow.emit(SuccessState(response.data.map {
                        Message(
                            ChatUser(it.sender, it.sender, it.sender, ColorProvider.getColor(), null),
                            it.recipient,
                            it.content,
                            it.contentType,
                            true,
                            it.attachments,
                            it.receivedAt.nanosecond.toLong(),
                            it.id.toLong()
                        )
                    })) // TODO redundant
                }

                is NetworkResponse.Failure -> {
                    _dataFlow.emit(ErrorState(response.throwable))
                    intOpenCommand.emit(
                        ModalOpenCommand(
                            "Failed",
                            response.throwable.message ?: "Failed to find chats"
                        )
                    )
                }

                is NetworkResponse.Unauthorized -> {
                    settings.clear()
                    intOpenCommand.emit(ModalOpenCommand("Unauthorized", "Please login again!"))
                    intOpenCommand.emit(NavigationOpenCommand(HarvestRoutes.Screen.LOGIN))
                }
            }
        }
    }
}