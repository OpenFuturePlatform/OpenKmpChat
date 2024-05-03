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
import com.mutualmobile.harvestKmp.domain.model.request.User
import com.mutualmobile.harvestKmp.domain.model.response.GetUserResponse
import com.mutualmobile.harvestKmp.features.datamodels.chatApiDataModels.ProfileDataModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ContactProfileViewModel : ViewModel() {
    var currentNavigationCommand: PraxisCommand? by mutableStateOf(null)
    var currentGroupDetailState: PraxisDataModel.DataState by mutableStateOf(PraxisDataModel.EmptyState)

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _currentUser = MutableStateFlow(User())
    val currentUser: StateFlow<User> = _currentUser

    var currentProfileName by mutableStateOf("")
    var currentGroupAdmin by mutableStateOf("")
    var currentGroupId by mutableStateOf("")
    var participants by mutableStateOf(emptyList<String>())

    var deleteMemberState: PraxisDataModel.DataState by mutableStateOf(PraxisDataModel.EmptyState)
        private set
    var isDeleteDialogVisible by mutableStateOf(false)
    var deleteMemberId by mutableStateOf("")
    var deleteMemberGroupId by mutableStateOf("")

    private val getProfileDataModel = ProfileDataModel()

    init {
        println("INIT CONTACT PROFILE VIEW MODEL")
        loadGroupDetails()
        with(getProfileDataModel) {
            observeDataState()
            observeNavigationCommands()
        }
    }

    private fun ProfileDataModel.observeNavigationCommands() =
        praxisCommand.onEach { newCommand ->
            currentNavigationCommand = newCommand
        }.launchIn(viewModelScope)

    private fun ProfileDataModel.observeDataState() {
        dataFlow.onEach { newState ->
            currentGroupDetailState = newState
        }.launchIn(viewModelScope)
    }

    fun resetAll(onComplete: () -> Unit = {}) {
        currentNavigationCommand = null
        currentGroupDetailState = PraxisDataModel.EmptyState
        deleteMemberState = PraxisDataModel.EmptyState
        isDeleteDialogVisible = false
        deleteMemberId = ""
        deleteMemberGroupId = ""
        onComplete()
    }

    private fun loadGroupDetails() {
        _loading.value = true
        with(getProfileDataModel) {
            dataFlow.onEach { newChatState ->
                if (newChatState is PraxisDataModel.SuccessState<*>) {
                    println("NEW STATE GROUP DETAILS WITH ${newChatState.data}")
                    val newMessage = newChatState.data as GroupDetails
                    participants = newMessage.participants
                    currentProfileName = newMessage.groupName
                    currentGroupAdmin = newMessage.groupCreator
                    currentGroupId = newMessage.groupId

                }
            }.launchIn(viewModelScope)
            activate()
        }
    }

    fun getGroupDetails(groupId: String, userState: PraxisDataModel.SuccessState<*>){
        val userResponse = userState.data as GetUserResponse
        _currentUser.value = User(id = userResponse.id, firstName = userResponse.firstName, lastName = userResponse.lastName, email = userResponse.email)
        getProfileDataModel.getGroup(groupId)
    }

    fun removeMemberClicked(onCompleted: () -> Unit = {}){
        if (deleteMemberId != "" && deleteMemberGroupId != "") {
            getProfileDataModel.removeMember(groupId = deleteMemberGroupId, memberId = deleteMemberId)
                .onEach { newState ->
                    if (newState is PraxisDataModel.SuccessState<*>) {
                        println("NEW STATE ON REMOVE MEMBER $newState with participants: $participants and member : $deleteMemberId")
                        participants = participants.minus(deleteMemberId)
                        deleteMemberState = newState
                    }
                }
                .launchIn(getProfileDataModel.dataModelScope)

            deleteMemberState = PraxisDataModel.EmptyState
            isDeleteDialogVisible = false

            onCompleted()
        }
    }

    fun onMemberClicked(user: String) {
        Log.d(TAG, "Click action user with $user and currentUser: ${currentUser.value}")
        getProfileDataModel.getUserPrivateChats(user, currentUser.value.email!!)
    }

}