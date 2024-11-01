package com.mutualmobile.harvestKmp.android.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mutualmobile.harvestKmp.datamodel.OpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import com.mutualmobile.harvestKmp.domain.model.request.TaskRequest
import com.mutualmobile.harvestKmp.domain.model.response.GetUserResponse
import com.mutualmobile.harvestKmp.domain.model.response.TaskResponse
import com.mutualmobile.harvestKmp.features.datamodels.userTaskDataModels.GetUserTasksDataModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class TaskScreenViewModel : ViewModel() {
    var taskListMap by mutableStateOf(emptyList<TaskResponse>())

    var filteredTaskListMap: List<TaskResponse> = emptyList()

    var textState by mutableStateOf(TextFieldValue(""))

    var currentTaskScreenState: OpenDataModel.DataState by mutableStateOf(OpenDataModel.EmptyState)
    var taskScreenNavigationCommands: OpenCommand? by mutableStateOf(null)

    private val getUserTasksDataModel = GetUserTasksDataModel()

    init {
        with(getUserTasksDataModel) {
            observeDataState()
            observeNavigationCommands()
        }
    }

    private fun GetUserTasksDataModel.observeNavigationCommands() {
        praxisCommand.onEach { newCommand ->
            taskScreenNavigationCommands = newCommand
        }.launchIn(viewModelScope)
    }

    private fun GetUserTasksDataModel.observeDataState() {
        dataFlow.onEach { taskState ->
            currentTaskScreenState = taskState
            when (taskState) {
                is OpenDataModel.SuccessState<*> -> {
                    println("TaskState $taskState")
                    taskListMap =
                        taskState.data as List<TaskResponse>
                }
                else -> Unit
            }
        }.launchIn(viewModelScope)
    }

    fun getUserTasks(userState: OpenDataModel.SuccessState<*>) {
        getUserTasksDataModel.getUserTasks(
            userId = (userState.data as GetUserResponse).email ?: ""
        )
    }

    fun saveUserTasks(taskRequest: TaskRequest) {
        getUserTasksDataModel.saveUserTasks(taskRequest)
    }

    fun resetAll(onComplete: () -> Unit = {}) {
        taskListMap = emptyList()
        filteredTaskListMap = emptyList()
        textState = TextFieldValue("")
        onComplete()
    }
}