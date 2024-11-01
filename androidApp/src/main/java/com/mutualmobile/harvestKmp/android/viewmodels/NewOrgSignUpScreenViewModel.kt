package com.mutualmobile.harvestKmp.android.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mutualmobile.harvestKmp.datamodel.OpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import com.mutualmobile.harvestKmp.features.datamodels.authApiDataModels.SignUpDataModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class NewOrgSignUpScreenViewModel : ViewModel() {
    var currentOpenCommand: OpenCommand? by mutableStateOf(null)

    var signUpState: OpenDataModel.DataState by mutableStateOf(OpenDataModel.EmptyState)

    private val signUpDataModel = SignUpDataModel()

    var currentWorkEmail by mutableStateOf("")
    var currentPassword by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var currentFirstName by mutableStateOf("")
    var currentLastName by mutableStateOf("")

    init {
        with(signUpDataModel) {
            observeDataState()
            observeNavigationCommands()
        }
    }

    private fun SignUpDataModel.observeNavigationCommands() =
        praxisCommand.onEach { newCommand ->
            currentOpenCommand = newCommand
        }.launchIn(viewModelScope)

    private fun SignUpDataModel.observeDataState() {
        dataFlow.onEach { newState ->
            signUpState = newState
        }.launchIn(viewModelScope)
    }

    fun signUp() {
        signUpDataModel.signUp(
            firstName = currentFirstName,
            lastName = currentLastName,
            email = currentWorkEmail,
            password = currentPassword
        )
    }

    fun resetAll(onComplete: () -> Unit) {
        currentOpenCommand = null
        signUpState = OpenDataModel.EmptyState
        currentWorkEmail = ""
        currentPassword = ""
        confirmPassword = ""
        onComplete()
    }
}