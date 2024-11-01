package com.mutualmobile.harvestKmp.android.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mutualmobile.harvestKmp.data.network.TAG
import com.mutualmobile.harvestKmp.datamodel.OpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import com.mutualmobile.harvestKmp.domain.model.request.User
import com.mutualmobile.harvestKmp.domain.model.response.GetUserResponse
import com.mutualmobile.harvestKmp.features.datamodels.chatApiDataModels.UserListDataModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class UserListViewModel : ViewModel() {
    var currentNavigationCommand: OpenCommand? by mutableStateOf(null)
    var currentHomeChatState: OpenDataModel.DataState by mutableStateOf(OpenDataModel.EmptyState)

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _currentUser = MutableStateFlow(User())
    val currentUser: StateFlow<User> = _currentUser

    private val _contacts = MutableStateFlow(listOf<User>())
    var contacts: StateFlow<List<User>> = _contacts

    private val getUserListDataModel = UserListDataModel()
    init {
        println("ChatRoomViewModel call init")
        getAllContacts()
        with(getUserListDataModel) {
            observeDataState()
            observeNavigationCommands()
        }
    }

    private fun UserListDataModel.observeNavigationCommands() =
        praxisCommand.onEach { newCommand ->
            currentNavigationCommand = newCommand
        }.launchIn(viewModelScope)

    private fun UserListDataModel.observeDataState() {
        dataFlow.onEach { newState ->
            currentHomeChatState = newState
        }.launchIn(viewModelScope)
    }

    private fun getAllContacts() {
        _loading.value = true
        with(getUserListDataModel) {
            dataFlow.onEach { newChatState ->
                if (newChatState is OpenDataModel.SuccessState<*>) {
                    val users = newChatState.data as List<User>
                    println("CONTACTS : $users")
                    _contacts.value = users
                }
            }.launchIn(viewModelScope)
            //activate()
        }
        _loading.value = false

    }

    fun resetAll(onComplete: () -> Unit = {}) {
        currentNavigationCommand = null
        currentHomeChatState = OpenDataModel.EmptyState
        onComplete()
    }

    fun getUserContacts(userState: OpenDataModel.SuccessState<*>){
        println("CONTACT STATE $userState")
        val userResponse = userState.data as GetUserResponse
        _currentUser.value = User(id = userResponse.id, firstName = userResponse.firstName, lastName = userResponse.lastName, email = userResponse.email)
        getUserListDataModel.getAllContacts()
    }

    private fun navigateToChatRoom(chatRoomUid: String) {
        Log.d(TAG, "Sending new chat room event, with chat room UID $chatRoomUid... with local user ${currentUser.value}")
        getUserListDataModel.getUserPrivateChats(chatRoomUid, currentUser.value.email!!)
    }

    fun onChatClicked(position: Int) {
        val chatUid = contacts.value[position].email
        navigateToChatRoom(chatUid!!)
    }
}