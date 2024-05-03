package com.mutualmobile.harvestKmp.features.datamodels.chatApiDataModels

import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes.Screen.withDetail
import com.mutualmobile.harvestKmp.datamodel.ModalPraxisCommand
import com.mutualmobile.harvestKmp.datamodel.NavigationPraxisCommand
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel
import com.mutualmobile.harvestKmp.di.ChatApiUseCaseComponent
import com.mutualmobile.harvestKmp.di.GroupApiUseCaseComponent
import com.mutualmobile.harvestKmp.di.SharedComponent
import com.mutualmobile.harvestKmp.domain.model.ChatUser
import com.mutualmobile.harvestKmp.domain.model.ColorProvider
import com.mutualmobile.harvestKmp.domain.model.Message
import com.mutualmobile.harvestKmp.domain.model.TextType
import com.mutualmobile.harvestKmp.domain.model.request.GroupCreateRequest
import com.mutualmobile.harvestKmp.domain.model.request.GroupMemberUpdateRequest
import com.mutualmobile.harvestKmp.domain.model.response.GetUserResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse
import dev.icerock.moko.graphics.Color
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class AddGroupDataModel : PraxisDataModel(), KoinComponent {

    private val _dataFlow = MutableSharedFlow<DataState>()
    val dataFlow = _dataFlow.asSharedFlow()

    var currentLoadingJob: Job? = null

    private val groupApiUseCaseComponent = GroupApiUseCaseComponent()
    private val createGroupUseCase = groupApiUseCaseComponent.provideCreateGroup()
    private val addMemberGroupUseCase = groupApiUseCaseComponent.provideAddMemberGroup()
    override fun activate() {
        println("ADD GROUP DATA MODEL ACTIVATE")
    }

    override fun destroy() {
        dataModelScope.cancel()
    }

    override fun refresh() {
        TODO("Not yet implemented")
    }

    fun createGroup(groupName: String, creator: String, participants: List<String>) {
        println("CREATE GROUP REQUEST with $groupName and $participants")
        val groupCreateRequest = GroupCreateRequest(name = groupName, creator = creator, participants = participants)
        dataModelScope.launch(exceptionHandler) {
            _dataFlow.emit(LoadingState)

            when (val createGroupResponse = createGroupUseCase(groupCreateRequest)) {

                is NetworkResponse.Success -> {
                    //_dataFlow.emit(SuccessState(createGroupResponse))
                    println("CREATE GROUP RESPONSE: ${createGroupResponse.data}")
                    intPraxisCommand.emit(
                        NavigationPraxisCommand(
                            screen = HarvestRoutes.Screen.CHAT
                        )
                    )
                }

                is NetworkResponse.Failure -> {
                    _dataFlow.emit(ErrorState(createGroupResponse.throwable))
                    intPraxisCommand.emit(
                        ModalPraxisCommand(
                            title = "Error",
                            createGroupResponse.throwable.message ?: "An Unknown error has happened"
                        )
                    )
                }

                is NetworkResponse.Unauthorized -> {
                    _dataFlow.emit(ErrorState(createGroupResponse.throwable))
                    intPraxisCommand.emit(
                        ModalPraxisCommand(
                            title = "Error",
                            createGroupResponse.throwable.message ?: "An Unknown error has happened"
                        )
                    )
                }
            }
        }
    }

    fun addMember(groupId: String, members: List<String>) {
        dataModelScope.launch {
            _dataFlow.emit(LoadingState)

            when (val response = addMemberGroupUseCase(request = GroupMemberUpdateRequest(groupId = groupId, users = members))) {
                is NetworkResponse.Success -> {

                    val profileScreen =
                        HarvestRoutes.Screen.CONTACT_PROFILE.withDetail(profileId = groupId, isGroup = true)

                    println("ADD MEMBER GROUP RESPONSE: ${response.data} and groupId: $groupId and route to => $profileScreen")

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
    }
}