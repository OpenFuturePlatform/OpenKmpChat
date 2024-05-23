package com.mutualmobile.harvestKmp.features.datamodels.chatApiDataModels

import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes.Screen.withOrgId
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes.Screen.withRecipient
import com.mutualmobile.harvestKmp.datamodel.ModalPraxisCommand
import com.mutualmobile.harvestKmp.datamodel.NavigationPraxisCommand
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel
import com.mutualmobile.harvestKmp.di.ChatApiUseCaseComponent
import com.mutualmobile.harvestKmp.domain.model.ChatUser
import com.mutualmobile.harvestKmp.domain.model.ColorProvider
import com.mutualmobile.harvestKmp.domain.model.DisplayChatRoom
import com.mutualmobile.harvestKmp.domain.model.Message
import com.mutualmobile.harvestKmp.domain.model.response.GetUserResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.koin.core.component.KoinComponent

class ChatGroupDataModel : PraxisDataModel(), KoinComponent {
    private val _dataFlow = MutableSharedFlow<DataState>()
    val dataFlow = _dataFlow.asSharedFlow()

    private var currentLoadingJob: Job? = null

    private val chatApiUseCaseComponent = ChatApiUseCaseComponent()
    private val getMessagesByUidUseCase = chatApiUseCaseComponent.provideGetMessagesByUid()
    private val getGroupMessagesByRecipientUseCase = chatApiUseCaseComponent.provideGroupMessagesByRecipient()

    override fun activate() {
        println("ACTIVATE CHAT GROUP")
    }

    override fun destroy() {
        dataModelScope.cancel()
    }

    override fun refresh() {
        TODO("Not yet implemented")
    }

    // NAVIGATE TO PRIVATE CHAT FROM LIST OF GROUP CHATS
    fun getUserPrivateChats(chatUid: String, isGroup: Boolean, recipient: String, sender: String) {
        dataModelScope.launch {
            _dataFlow.emit(LoadingState)
            when (val response = getMessagesByUidUseCase(chatUid = chatUid, isGroup = isGroup)) {
                is NetworkResponse.Success -> {
                    println("GET GROUP PRIVATE CHATS: ${response.data} with recipient: $recipient and sender: $sender")

                    intPraxisCommand.emit(
                        NavigationPraxisCommand(
                            screen = HarvestRoutes.Screen.CHAT_PRIVATE.withRecipient(chatUid, isGroup, recipient, sender)
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

    //TOTAL CURRENT USER GROUPED CHAT LIST FETCH
    fun getUserGroupChats(username: String) {
        currentLoadingJob?.cancel()
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)

            when (val response =
                getGroupMessagesByRecipientUseCase(username = username)) {

                is NetworkResponse.Success -> {
                    println("GROUP RESPONSE ${response.data}")
                    _dataFlow.emit(SuccessState(response.data))
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