package com.mutualmobile.harvestKmp.features.datamodels.chatApiDataModels

import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes.Screen.withRecipient
import com.mutualmobile.harvestKmp.datamodel.ModalOpenCommand
import com.mutualmobile.harvestKmp.datamodel.NavigationOpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import com.mutualmobile.harvestKmp.di.ChatApiUseCaseComponent
import com.mutualmobile.harvestKmp.di.UserApiUseCaseComponent
import com.mutualmobile.harvestKmp.domain.model.request.User
import com.mutualmobile.harvestKmp.features.NetworkResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class UserListDataModel : OpenDataModel(), KoinComponent {
    private val _dataFlow = MutableSharedFlow<DataState>()
    val dataFlow = _dataFlow.asSharedFlow()

    private var currentLoadingJob: Job? = null

    private val userApiUseCaseComponent = UserApiUseCaseComponent()
    private val getAllContactsUseCase = userApiUseCaseComponent.provideGetAllContacts()
    private val chatApiUseCaseComponent = ChatApiUseCaseComponent()
    private val getPrivateMessagesByRecipientUseCase = chatApiUseCaseComponent.providePrivateMessagesByRecipient()


    override fun activate() {
        getAllContacts()
    }

    override fun destroy() {
        TODO("Not yet implemented")
    }

    override fun refresh() {
        TODO("Not yet implemented")
    }

    fun getAllContacts() {
        currentLoadingJob?.cancel()
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)
            when (val response = getAllContactsUseCase()) {
                is NetworkResponse.Success -> {
                    println("CONTACTS RESPONSE ${response.data}")
                    _dataFlow.emit(SuccessState(response.data.map {
                        User(
                            id = it.id,
                            email = it.email,
                            firstName = it.firstName,
                            lastName = it.lastName
                        )
                    }));
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

    fun getUserPrivateChats(recipient: String, sender: String) {
        currentLoadingJob?.cancel()
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)
            when (val response = getPrivateMessagesByRecipientUseCase(receiver = recipient, sender = sender)) {
                is NetworkResponse.Success -> {
                    println("PRIVATE CHATS: ${response.data} with recipient: $recipient and sender: $sender")

                    intOpenCommand.emit(
                        NavigationOpenCommand(
                            screen = HarvestRoutes.Screen.CHAT_PRIVATE.withRecipient(
                                recipient,
                                false,
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
                    settings.clear()
                    intOpenCommand.emit(ModalOpenCommand("Unauthorized", "Please login again!"))
                    intOpenCommand.emit(NavigationOpenCommand(HarvestRoutes.Screen.LOGIN))
                }
            }

        }
    }
}