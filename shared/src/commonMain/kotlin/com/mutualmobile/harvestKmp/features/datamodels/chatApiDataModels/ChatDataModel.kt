package com.mutualmobile.harvestKmp.features.datamodels.chatApiDataModels

import com.mutualmobile.harvestKmp.datamodel.ModalPraxisCommand
import com.mutualmobile.harvestKmp.datamodel.NavigationPraxisCommand
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel
import com.mutualmobile.harvestKmp.di.ChatApiUseCaseComponent
import com.mutualmobile.harvestKmp.di.SharedComponent
import com.mutualmobile.harvestKmp.domain.model.ChatUser
import com.mutualmobile.harvestKmp.domain.model.Message
import com.mutualmobile.harvestKmp.features.NetworkResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class ChatDataModel : PraxisDataModel(), KoinComponent {

    private val _dataFlow = MutableSharedFlow<DataState>()
    val dataFlow = _dataFlow.asSharedFlow()

    private var currentLoadingJob: Job? = null
    private val chatLocal = SharedComponent().provideChatLocal()

    private val chatApiUseCaseComponent = ChatApiUseCaseComponent()
    private val getMessagesByRecipientUseCase = chatApiUseCaseComponent.provideGetMessagesByRecipient()
    private val createMessagesUseCase = chatApiUseCaseComponent.provideCreateMessages()
    override fun activate() {
        getChat()
    }

    override fun destroy() {
        dataModelScope.cancel()
    }

    override fun refresh() {
        TODO("Not yet implemented")
    }

    fun getChat(forceFetchFromNetwork: Boolean = false) {
        currentLoadingJob?.cancel()
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)
            if (!forceFetchFromNetwork) {
                chatLocal.getAll().let { nnChats ->
                    val messages = nnChats
                        .map {
                            Message(
                                ChatUser(it.userId!!, "owner", picture = null),
                                it.content!!,
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
                intPraxisCommand.emit(NavigationPraxisCommand(""))
            }
        }
    }

    fun getUserChats(username: String){
        dataModelScope.launch {
            _dataFlow.emit(LoadingState)

            when (val response = getMessagesByRecipientUseCase(username = username)) {
                is NetworkResponse.Success -> {
                    _dataFlow.emit(SuccessState(response.data)) // TODO redundant
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
                    intPraxisCommand.emit(NavigationPraxisCommand(""))
                }
            }
        }
    }
}