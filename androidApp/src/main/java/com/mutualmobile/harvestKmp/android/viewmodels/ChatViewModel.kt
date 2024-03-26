package com.mutualmobile.harvestKmp.android.viewmodels

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mutualmobile.harvestKmp.datamodel.PraxisCommand
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel
import com.mutualmobile.harvestKmp.db.flattenToList
import com.mutualmobile.harvestKmp.di.SharedComponent
import com.mutualmobile.harvestKmp.domain.model.DisplayChatRoom
import com.mutualmobile.harvestKmp.domain.model.Message
import com.mutualmobile.harvestKmp.domain.model.response.GetUserResponse
import com.mutualmobile.harvestKmp.features.datamodels.authApiDataModels.GetUserDataModel
import com.mutualmobile.harvestKmp.features.datamodels.authApiDataModels.LoginDataModel
import com.mutualmobile.harvestKmp.features.datamodels.chatApiDataModels.ChatDataModel
import db.Harvest_chat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class ChatViewModel : ViewModel() {
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private var _chats = MutableStateFlow(listOf<Harvest_chat>())
    val chats: StateFlow<List<Harvest_chat>> = _chats

    private val getChatDataModel = ChatDataModel()
    private val chatLocal = SharedComponent().provideChatLocal()

    init {
        getCurrentUserChats()
    }

    @SuppressLint("SuspiciousIndentation")
    private fun getCurrentUserChats() {
        _loading.value = true
        val now: Instant = Clock.System.now()
//        with(getChatDataModel) {
//            dataFlow.onEach { newUserState ->
//                if (newUserState is PraxisDataModel.SuccessState<*>) {
//                    _chats = newUserState.data as MutableStateFlow<List<Harvest_chat>>
//                }
//            }.launchIn(viewModelScope)
//            activate()
//        }
          GlobalScope.launch {
              _chats.value = chatLocal.getAll().flattenToList()
          }
        //_chats.value = mutableListOf( chatLocal.getChat()!!)
    }

    fun saveChat(message: Message) {
        getChatDataModel.saveChat(message)
    }

}