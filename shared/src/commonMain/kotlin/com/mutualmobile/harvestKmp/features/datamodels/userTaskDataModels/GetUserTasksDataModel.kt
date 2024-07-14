package com.mutualmobile.harvestKmp.features.datamodels.userTaskDataModels

import com.mutualmobile.harvestKmp.datamodel.ModalPraxisCommand
import com.mutualmobile.harvestKmp.datamodel.NavigationPraxisCommand
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel
import com.mutualmobile.harvestKmp.di.UserTaskUseCaseComponent
import com.mutualmobile.harvestKmp.domain.model.request.TaskRequest
import com.mutualmobile.harvestKmp.features.NetworkResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class GetUserTasksDataModel() :
    PraxisDataModel(), KoinComponent {
  private val _dataFlow = MutableSharedFlow<DataState>()
    val dataFlow = _dataFlow.asSharedFlow()

    private var currentLoadingJob: Job? = null
    private val userTaskUseCaseComponent = UserTaskUseCaseComponent()
    private val getUserTasksUseCase = userTaskUseCaseComponent.provideGetUserTasksUseCase()
    private val saveUserTasksUseCase = userTaskUseCaseComponent.provideSaveUserTasksUseCase()

    override fun activate() {
    }

    override fun destroy() {
        dataModelScope.cancel()
    }

    override fun refresh() {
    }

    fun getUserTasks(
        userId: String?
    ) {
        currentLoadingJob?.cancel()
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)
            when (val response =
                getUserTasksUseCase(
                    username = userId!!
                )) {
                is NetworkResponse.Success -> {
                    _dataFlow.emit(SuccessState(response.data))
                }
                is NetworkResponse.Failure -> {
                    _dataFlow.emit(ErrorState(response.throwable))
                }
                is NetworkResponse.Unauthorized -> {
                    settings.clear()
                    intPraxisCommand.emit(ModalPraxisCommand("Unauthorized", "Please login again!"))
                    intPraxisCommand.emit(NavigationPraxisCommand(""))
                }
            }
        }
    }

    fun saveUserTasks(
        taskRequest: TaskRequest
    ) {
        currentLoadingJob?.cancel()
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)
            when (val response =
                saveUserTasksUseCase(
                    taskRequest = taskRequest
                )) {
                is NetworkResponse.Success -> {
                    _dataFlow.emit(SuccessState(listOf(response.data)))
                }
                is NetworkResponse.Failure -> {
                    _dataFlow.emit(ErrorState(response.throwable))
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
