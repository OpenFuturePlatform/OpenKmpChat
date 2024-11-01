package com.mutualmobile.harvestKmp.android.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mutualmobile.harvestKmp.datamodel.OpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import com.mutualmobile.harvestKmp.features.datamodels.orgForgotPasswordApiDataModels.ForgotPasswordDataModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ForgotPasswordViewModel : ViewModel() {
    var forgotPasswordState: OpenDataModel.DataState by mutableStateOf(OpenDataModel.EmptyState)

    var forgotPasswordNavigationCommand: OpenCommand? by mutableStateOf(null)

    private val forgotPasswordDataModel = ForgotPasswordDataModel()

    var currentWorkEmail by mutableStateOf("")

    init {
        with(forgotPasswordDataModel) {
            observeDataState()
            observeNavigationCommands()
        }
    }

    private fun ForgotPasswordDataModel.observeNavigationCommands() =
        praxisCommand.onEach { newCommand ->
            forgotPasswordNavigationCommand = newCommand
        }.launchIn(viewModelScope)

    private fun ForgotPasswordDataModel.observeDataState() {
        dataFlow.onEach { passwordState ->
            forgotPasswordState = passwordState
        }.launchIn(viewModelScope)
    }

    fun forgotPassword() {
        forgotPasswordDataModel.forgotPassword(email = currentWorkEmail.trim())
    }

    fun resetAll(onComplete: () -> Unit = {}) {
        forgotPasswordState = OpenDataModel.EmptyState
        forgotPasswordNavigationCommand = null
        currentWorkEmail = ""
        onComplete()
    }
}