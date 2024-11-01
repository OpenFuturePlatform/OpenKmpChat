package com.mutualmobile.harvestKmp.android.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mutualmobile.harvestKmp.datamodel.OpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import com.mutualmobile.harvestKmp.domain.model.response.ApiResponse
import com.mutualmobile.harvestKmp.domain.model.response.GetUserResponse
import com.mutualmobile.harvestKmp.domain.model.response.OrgProjectResponse
import com.mutualmobile.harvestKmp.features.datamodels.userProjectDataModels.GetUserAssignedProjectsDataModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ProjectScreenViewModel : ViewModel() {
    var projectListMap by mutableStateOf(emptyList<OrgProjectResponse>())

    var filteredProjectListMap: List<OrgProjectResponse> = emptyList()

    var textState by mutableStateOf(TextFieldValue(""))

    var currentProjectScreenState: OpenDataModel.DataState by mutableStateOf(OpenDataModel.EmptyState)
    var projectScreenNavigationCommands: OpenCommand? by mutableStateOf(null)

    private val getUserAssignedProjectsDataModel = GetUserAssignedProjectsDataModel()

    init {
        with(getUserAssignedProjectsDataModel) {
            observeDataState()
            observeNavigationCommands()
        }
    }

    private fun GetUserAssignedProjectsDataModel.observeNavigationCommands() {
        praxisCommand.onEach { newCommand ->
            projectScreenNavigationCommands = newCommand
        }.launchIn(viewModelScope)
    }

    private fun GetUserAssignedProjectsDataModel.observeDataState() {
        dataFlow.onEach { projectState ->
            currentProjectScreenState = projectState
            when (projectState) {
                is OpenDataModel.SuccessState<*> -> {
                    projectListMap =
                        (projectState.data as ApiResponse<*>).data as List<OrgProjectResponse>
                }
                else -> Unit
            }
        }.launchIn(viewModelScope)
    }

    fun getUserAssignedProjects(userState: OpenDataModel.SuccessState<*>) {
        getUserAssignedProjectsDataModel.getUserAssignedProjects(
            userId = (userState.data as GetUserResponse).id ?: ""
        )
    }

    fun resetAll(onComplete: () -> Unit = {}) {
        projectListMap = emptyList()
        filteredProjectListMap = emptyList()
        textState = TextFieldValue("")
        onComplete()
    }
}