package com.mutualmobile.harvestKmp.features.datamodels.chatApiDataModels

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
import com.mutualmobile.harvestKmp.domain.model.request.GroupCreateRequest
import com.mutualmobile.harvestKmp.domain.model.request.GroupMemberUpdateRequest
import com.mutualmobile.harvestKmp.domain.model.response.GetUserResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse
import dev.icerock.moko.graphics.Color
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class ProfileDataModel : PraxisDataModel(), KoinComponent {
    private val _dataFlow = MutableSharedFlow<DataState>()
    val dataFlow = _dataFlow.asSharedFlow()

    val contactProfileId = MutableStateFlow("")
    val isGroup = MutableStateFlow(false)

    var currentLoadingJob: Job? = null

    private val groupApiUseCaseComponent = GroupApiUseCaseComponent()
    private val getGroupUseCase = groupApiUseCaseComponent.provideGetGroup()
    private val addMemberGroupUseCase = groupApiUseCaseComponent.provideAddMemberGroup()
    private val removeMemberGroupUseCase = groupApiUseCaseComponent.provideRemoveMemberGroup()

    private val chatApiUseCaseComponent = ChatApiUseCaseComponent()
    private val getPrivateMessagesByRecipientUseCase = chatApiUseCaseComponent.providePrivateMessagesByRecipient()
    override fun activate() {
        println("GET PROFILE DATA MODEL ACTIVATE")
    }

    override fun destroy() {
        dataModelScope.cancel()
    }

    override fun refresh() {
        TODO("Not yet implemented")
    }
    fun getGroup(groupId: String) {
        println("GET GROUP REQUEST with $groupId")
        contactProfileId.value = groupId
        currentLoadingJob?.cancel()
        currentLoadingJob = dataModelScope.launch(exceptionHandler) {
            _dataFlow.emit(LoadingState)

            when (val getGroupResponse = getGroupUseCase(
                groupId = groupId
            )) {
                is NetworkResponse.Success -> {
                    _dataFlow.emit(SuccessState(GroupDetails(
                        groupId = getGroupResponse.data.id.toString(),
                        groupName = getGroupResponse.data.name,
                        groupAvatar = getGroupResponse.data.avatar,
                        groupCreator = getGroupResponse.data.creator,
                        participants = getGroupResponse.data.participants)))
                    println("GET GROUP RESPONSE: ${getGroupResponse.data}")
                }

                is NetworkResponse.Failure -> {
                    _dataFlow.emit(ErrorState(getGroupResponse.throwable))
                    intPraxisCommand.emit(
                        ModalPraxisCommand(
                            title = "Error",
                            getGroupResponse.throwable.message ?: "An Unknown error has happened"
                        )
                    )
                }

                is NetworkResponse.Unauthorized -> {
                    _dataFlow.emit(ErrorState(getGroupResponse.throwable))
                    intPraxisCommand.emit(
                        ModalPraxisCommand(
                            title = "Error",
                            getGroupResponse.throwable.message ?: "An Unknown error has happened"
                        )
                    )
                }
            }
        }
    }

    fun removeMember(groupId: String, memberId: String) : Flow<DataState> {
        println("remove member REQUEST with $groupId")
        return flow {
            this.emit(LoadingState)
            when (val response = removeMemberGroupUseCase(
                request = GroupMemberUpdateRequest(groupId = groupId, users = listOf(memberId))
            )) {
                is NetworkResponse.Success -> {
                    println("REMOVE MEMBER GROUP RESPONSE: ${response.data} and groupId: $groupId")

                    this.emit(SuccessState(response.data))
                    intPraxisCommand.emit(ModalPraxisCommand("Success",  ""))
                }

                is NetworkResponse.Failure -> {
                    _dataFlow.emit(ErrorState(response.throwable))
                    intPraxisCommand.emit(
                        ModalPraxisCommand(
                            title = "Error",
                            response.throwable.message ?: "An Unknown error has happened"
                        )
                    )
                }

                is NetworkResponse.Unauthorized -> {
                    _dataFlow.emit(ErrorState(response.throwable))
                    intPraxisCommand.emit(
                        ModalPraxisCommand(
                            title = "Error",
                            response.throwable.message ?: "An Unknown error has happened"
                        )
                    )
                }
            }
        }
//        dataModelScope.launch(exceptionHandler) {
//            _dataFlow.emit(LoadingState)
//
//            when (val response = removeMemberGroupUseCase(
//                request = GroupMemberUpdateRequest(groupId = groupId, users = listOf(memberId))
//            )) {
//                is NetworkResponse.Success -> {
//                    println("REMOVE MEMBER GROUP RESPONSE: ${response.data} and groupId: $groupId")
//
//                    intPraxisCommand.emit(
//                        NavigationPraxisCommand(
//                            screen = HarvestRoutes.Screen.CONTACT_PROFILE.withDetail(profileId = groupId, isGroup = isGroup.value)
//                        )
//                    )
//                }
//
//                is NetworkResponse.Failure -> {
//                    _dataFlow.emit(ErrorState(response.throwable))
//                    intPraxisCommand.emit(
//                        ModalPraxisCommand(
//                            title = "Error",
//                            response.throwable.message ?: "An Unknown error has happened"
//                        )
//                    )
//                }
//
//                is NetworkResponse.Unauthorized -> {
//                    _dataFlow.emit(ErrorState(response.throwable))
//                    intPraxisCommand.emit(
//                        ModalPraxisCommand(
//                            title = "Error",
//                            response.throwable.message ?: "An Unknown error has happened"
//                        )
//                    )
//                }
//            }
//        }
    }

    fun getUserPrivateChats(recipient: String, sender: String) {
        currentLoadingJob?.cancel()
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)
            when (val response = getPrivateMessagesByRecipientUseCase(receiver = recipient, sender = sender)) {
                is NetworkResponse.Success -> {
                    println("PRIVATE CHATS: ${response.data} with recipient: $recipient and sender: $sender")

                    intPraxisCommand.emit(
                        NavigationPraxisCommand(
                            screen = HarvestRoutes.Screen.CHAT_PRIVATE.withRecipient(recipient, false, recipient, sender)
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
                    intPraxisCommand.emit(NavigationPraxisCommand(""))
                }
            }

        }
    }
}