package com.mutualmobile.harvestKmp.features.datamodels.chatApiDataModels

import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes
import com.mutualmobile.harvestKmp.datamodel.ModalPraxisCommand
import com.mutualmobile.harvestKmp.datamodel.NavigationPraxisCommand
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel
import com.mutualmobile.harvestKmp.di.ChatApiUseCaseComponent
import com.mutualmobile.harvestKmp.di.SharedComponent
import com.mutualmobile.harvestKmp.domain.model.*
import com.mutualmobile.harvestKmp.domain.model.response.GetUserResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse
import dev.icerock.moko.graphics.Color
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class ChatDataModel : PraxisDataModel(), KoinComponent {

    val _dataFlow = MutableSharedFlow<DataState>()
    val dataFlow = _dataFlow.asSharedFlow()

    var _canSendMessage = MutableSharedFlow<Boolean>()
    val canSendMessage = _canSendMessage.asSharedFlow()

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

    private fun getChat(forceFetchFromNetwork: Boolean = false) {
        currentLoadingJob?.cancel()
        println("GET CHATS IN ACTIVATE")
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)
            if (!forceFetchFromNetwork) {
                chatLocal.getAll().let { nnChats ->
                    val messages = nnChats
                        .map {
                            Message(
                                ChatUser(it.userId!!, "owner", email = it.userId,  picture = null),
                                it.userId,
                                it.content!!,
                                "",
                                TextType.TEXT,
                                false,
                                it.time!!,
                                it.uid.toLong()
                            )
                        }
                        .toList()
                    _dataFlow.emit(
                        SuccessState(
                            messages
                        )
                    )
                    return@launch
                }
            }
        }
    }

    fun saveChat(message: Message) {
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)

            chatLocal.saveChat(message)

            saveRemote(message)

            val res = mutableListOf(message)
            _dataFlow.emit(SuccessState(res))
        }
    }

    fun saveChatGptChat(message: Message) {

        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)
            val res = mutableListOf(message)
            _dataFlow.emit(SuccessState(res))
            _canSendMessage.emit(false)
            val aiMessage = AiMessage(sender = message.user.email, contentType = message.type, body = message.text)
            when (val response = createAiMessagesUseCase(message = aiMessage)) {
                is NetworkResponse.Success -> {
                    println("AI RESPONSE: ${response.data}")
                    val aiUser = ChatUser(name = "AI_ASSISTANT", id = "", email = "openai@openfuture.io", picture = null)
                    val responseMessage = Message(user = aiUser, recipient = response.data.recipient, text = response.data.content, attachmentUrl = "", type = response.data.contentType)
                    _dataFlow.emit(SuccessState(mutableListOf(responseMessage)))
                    _canSendMessage.emit(true)
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

    private suspend fun saveRemote(message: Message) {
        when (val response = createMessagesUseCase(message = message)) {
            is NetworkResponse.Success -> {
                //_dataFlow.emit(SuccessState(response.data)) // TODO redundant
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

    fun getUserChats(username: String){

        dataModelScope.launch {
            _dataFlow.emit(LoadingState)

            when (val response = getMessagesByRecipientUseCase(username = username)) {
                is NetworkResponse.Success -> {
                    _dataFlow.emit(SuccessState(response.data.map {
                        Message(
                            ChatUser(it.sender, it.sender, it.sender, ColorProvider.getColor(), null),
                            it.recipient,
                            it.content,
                            "",
                            it.contentType,
                            true,
                            it.receivedAt.nanosecond.toLong(),
                            it.id.toLong())
                    })) // TODO redundant
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