package com.mutualmobile.harvestKmp.features.datamodels.chatApiDataModels

import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes.Screen.withDetail
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

class GetGroupDataModel : PraxisDataModel(), KoinComponent {
    private val _dataFlow = MutableSharedFlow<DataState>()
    val dataFlow = _dataFlow.asSharedFlow()

    val contactProfileId = MutableStateFlow("")
    val isGroup = MutableStateFlow(false)

    var currentLoadingJob: Job? = null

    private val groupApiUseCaseComponent = GroupApiUseCaseComponent()
    private val getGroupUseCase = groupApiUseCaseComponent.provideGetGroup()
    private val addMemberGroupUseCase = groupApiUseCaseComponent.provideAddMemberGroup()
    private val removeMemberGroupUseCase = groupApiUseCaseComponent.provideRemoveMemberGroup()
    override fun activate() {
        println("GET GROUP DATA MODEL ACTIVATE")
    }

    override fun destroy() {
        dataModelScope.cancel()
    }

    override fun refresh() {
        TODO("Not yet implemented")
    }

}