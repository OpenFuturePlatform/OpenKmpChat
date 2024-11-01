package com.mutualmobile.harvestKmp.datamodel

import com.mutualmobile.harvestKmp.di.SharedComponent
import com.rickclephas.kmp.nativecoroutines.NativeCoroutineScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

abstract class OpenDataModel(
    @NativeCoroutineScope
    var dataModelScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {

    protected val settings = SharedComponent().provideSettings()

    internal val intOpenCommand = MutableSharedFlow<OpenCommand>()
    val praxisCommand = intOpenCommand.asSharedFlow()


    protected val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        dataModelScope.launch { intOpenCommand.emit(
            ModalOpenCommand(
                title = "Error",
                throwable.message ?: "An Unknown error has happened"
            )
        ) }
    }

    abstract fun activate()
    abstract fun destroy()
    abstract fun refresh()

    sealed class DataState
    object LoadingState : DataState()
    object UploadLoadingState : DataState()
    object UploadCompleteState : DataState()
    object EmptyState : DataState()
    object Complete : DataState()
    data class SuccessState<T>(
        val data: T,
    ) : DataState()
    data class WalletBalanceSuccessState<T>(
        val data: T,
    ) : DataState()
    data class AssistantSuccessState<T>(
        val data: T,
    ) : DataState()
    data class AssistantReminderSuccessState<T>(
        val data: T,
    ) : DataState()
    data class AssistantTodoSuccessState<T>(
        val data: T,
    ) : DataState()
    class ErrorState(var throwable: Throwable) : DataState()
    object LogoutInProgress : DataState()
}

