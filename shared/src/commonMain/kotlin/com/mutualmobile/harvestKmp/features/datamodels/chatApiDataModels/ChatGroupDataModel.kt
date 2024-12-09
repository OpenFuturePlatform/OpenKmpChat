package com.mutualmobile.harvestKmp.features.datamodels.chatApiDataModels

import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes.Screen.withRecipient
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes.Screen.withWalletDetail
import com.mutualmobile.harvestKmp.datamodel.ModalOpenCommand
import com.mutualmobile.harvestKmp.datamodel.NavigationOpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import com.mutualmobile.harvestKmp.di.ChatApiUseCaseComponent
import com.mutualmobile.harvestKmp.features.NetworkResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class ChatGroupDataModel : OpenDataModel(), KoinComponent {
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
        executeJob {
            _dataFlow.emit(LoadingState)

//            intOpenCommand.emit(
//                NavigationOpenCommand(
//                    screen = HarvestRoutes.Screen.WALLET_DETAIL.withWalletDetail("0x9a6fADEde3eA995eEF711814ffA6d62496C8cB44", "BNB", "YVipJR6+2IcTKyM0z8asXUbxWyDA1kaewEOvXExMaVa18Ofkcnw34l6kKVyaI1OhymJqOIJr4ATUmYzlu5z2WQ==")
//                )
//            )
            when (val response = getMessagesByUidUseCase(chatUid = chatUid, isGroup = isGroup)) {
                is NetworkResponse.Success -> {
                    println("GET GROUP PRIVATE CHATS: ${response.data} with recipient: $recipient and sender: $sender")

                    intOpenCommand.emit(
                        NavigationOpenCommand(
                            screen = HarvestRoutes.Screen.CHAT_PRIVATE.withRecipient(
                                chatUid,
                                isGroup,
                                recipient,
                                sender
                            )
                        )
                    )

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
                    handleUnauthorized()

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
                    //println("GROUP RESPONSE ${response.data}")
                    _dataFlow.emit(SuccessState(response.data))
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

    private suspend fun handleUnauthorized() {
        settings.clear()
        intOpenCommand.emit(ModalOpenCommand("Unauthorized", "Please login again!"))
        intOpenCommand.emit(NavigationOpenCommand(HarvestRoutes.Screen.LOGIN))
    }

    private fun executeJob(block: suspend () -> Unit) {
        currentLoadingJob?.cancel()
        currentLoadingJob = dataModelScope.launch { block() }
    }
}