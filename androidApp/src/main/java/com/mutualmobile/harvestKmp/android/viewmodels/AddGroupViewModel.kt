package com.mutualmobile.harvestKmp.android.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mutualmobile.harvestKmp.data.network.TAG
import com.mutualmobile.harvestKmp.datamodel.PraxisCommand
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel
import com.mutualmobile.harvestKmp.domain.model.GroupDetails
import com.mutualmobile.harvestKmp.domain.model.Message
import com.mutualmobile.harvestKmp.domain.model.request.User
import com.mutualmobile.harvestKmp.domain.model.response.GetUserResponse
import com.mutualmobile.harvestKmp.features.datamodels.authApiDataModels.LoginDataModel
import com.mutualmobile.harvestKmp.features.datamodels.chatApiDataModels.AddGroupDataModel
import com.mutualmobile.harvestKmp.features.datamodels.chatApiDataModels.ChatGroupDataModel
import com.mutualmobile.harvestKmp.features.datamodels.chatApiDataModels.GetGroupDataModel
import com.mutualmobile.harvestKmp.features.datamodels.chatApiDataModels.UserListDataModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.streams.toList

class AddGroupViewModel : ViewModel() {
    var currentNavigationCommand: PraxisCommand? by mutableStateOf(null)
    var currentAddGroupState: PraxisDataModel.DataState by mutableStateOf(PraxisDataModel.EmptyState)

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _currentUser = MutableStateFlow(User())
    val currentUser: StateFlow<User> = _currentUser

    var currentGroupName by mutableStateOf("")
    var participants by mutableStateOf(emptyList<User>())

    private val addGroupDataModel = AddGroupDataModel()

    init {
        println("AddGroupViewModel call init")
        getGroupDetails()
        with(addGroupDataModel) {
            observeDataState()
            observeNavigationCommands()
        }
    }

    private fun AddGroupDataModel.observeDataState() {
        dataFlow.onEach { newState ->
            currentAddGroupState = newState
        }.launchIn(viewModelScope)
    }
    private fun AddGroupDataModel.observeNavigationCommands() =
        praxisCommand.onEach { newCommand ->
            currentNavigationCommand = newCommand
        }.launchIn(viewModelScope)

    private fun getGroupDetails() {
        _loading.value = true
        with(addGroupDataModel) {
            dataFlow.onEach { newState ->
                println("ADD GROUP MEMBER STATE")
                if (newState is PraxisDataModel.SuccessState<*>) {
                    println("NEW GROUP MEMBER STATE ${newState.data}")
                }
            }.launchIn(viewModelScope)
            activate()
        }
        _loading.value = false
    }

    fun resetAll(onComplete: () -> Unit = {}) {
        currentNavigationCommand = null
        currentAddGroupState = PraxisDataModel.EmptyState
        onComplete()
    }

    fun getGroupParticipants(userState: PraxisDataModel.SuccessState<*>){
        println("Participant STATE $userState")
        val userResponse = userState.data as GetUserResponse
        _currentUser.value = User(id = userResponse.id, firstName = userResponse.firstName, lastName = userResponse.lastName, email = userResponse.email)
    }

    fun onChatClicked(user: User) {
        Log.d(TAG, "Add new user to group with ${user.email}")
        participants = if (!participants.contains(user))
            participants.plus(user)
        else
            participants.minus(user)
        println("Total participant size : ${participants.size}")
    }

    fun createGroupWithParticipant() {
        addGroupDataModel.createGroup(
            groupName = currentGroupName,
            creator = currentUser.value.email!!,
            participants = participants.map { it.email!! }.toList()
        )
    }

    fun addMemberToGroup(groupId: String) {
        println("Add members to $groupId with members $participants")
        addGroupDataModel.addMember(
            groupId = groupId,
            members = participants.map { it.email!! }.toList()
        )
    }
}