package com.mutualmobile.harvestKmp.features.datamodels.chatApiDataModels

import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel
import com.mutualmobile.harvestKmp.di.SharedComponent
import com.mutualmobile.harvestKmp.domain.model.Message
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class ChatDataModel : PraxisDataModel(), KoinComponent {

    private val _dataFlow = MutableSharedFlow<DataState>()
    val dataFlow = _dataFlow.asSharedFlow()

    private var currentLoadingJob: Job? = null
    private val chatLocal = SharedComponent().provideChatLocal()
    override fun activate() {
        getChat()
    }

    override fun destroy() {
        dataModelScope.cancel()
    }

    override fun refresh() {
        TODO("Not yet implemented")
    }

    fun getChat(forceFetchFromNetwork: Boolean = false) {
        currentLoadingJob?.cancel()
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)
            if (!forceFetchFromNetwork) {
                chatLocal.getAll().let { nnChats ->
                    _dataFlow.emit(
                        SuccessState(
                            nnChats
                        )
                    )
                    return@launch
                }
            }
        }
    }

    fun saveChat(message: Message) {
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)
            chatLocal.saveChat(message)
            _dataFlow.emit(SuccessState(message))
        }
    }
}