package com.mutualmobile.harvestKmp.android.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.mutualmobile.harvestKmp.android.ui.utils.SecurityUtils.generateSecretKey
import com.mutualmobile.harvestKmp.android.ui.utils.SecurityUtils.getSecretKey
import com.mutualmobile.harvestKmp.datamodel.OpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import com.mutualmobile.harvestKmp.domain.model.response.LoginResponse
import com.mutualmobile.harvestKmp.features.datamodels.authApiDataModels.GetUserDataModel
import com.mutualmobile.harvestKmp.features.datamodels.authApiDataModels.LoginDataModel
import com.mutualmobile.harvestKmp.features.datamodels.authApiDataModels.UserDashboardDataModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LoginViewModel : ViewModel() {
    var currentWorkEmail by mutableStateOf("")
    var currentPassword by mutableStateOf("")

    var currentNavigationCommand: OpenCommand? by mutableStateOf(null)
    var currentLoginState: OpenDataModel.DataState by mutableStateOf(OpenDataModel.EmptyState)
    var currentLogoutState: OpenDataModel.DataState by mutableStateOf(OpenDataModel.EmptyState)

    private val loginDataModel = LoginDataModel()
    private val userDashboardDataModel = UserDashboardDataModel()
    private val getUserDataModel = GetUserDataModel()

    var currentErrorMsg: String? by mutableStateOf(null)

    init {
        with(loginDataModel) {
            observeDataState()
            observeNavigationCommands()
        }
        with(userDashboardDataModel) {
            observeDataState()
            observeNavigationCommands()
        }
    }

    private fun LoginDataModel.observeNavigationCommands() =
        praxisCommand.onEach { newCommand ->
            currentNavigationCommand = newCommand
        }.launchIn(viewModelScope)

    private fun LoginDataModel.observeDataState() {
        dataFlow.onEach { loginState ->
            currentLoginState = loginState
            if (loginState is OpenDataModel.SuccessState<*>) {
                val loginResponse = loginState.data as? LoginResponse

                getSecretKey("harvestKey")

                FirebaseMessaging.getInstance().token.addOnSuccessListener { fcmToken ->
                    if (loginResponse != null) {
                        loginDataModel.saveFcmToken(fcmToken, loginResponse.userId!!)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun UserDashboardDataModel.observeNavigationCommands() =
        praxisCommand.onEach { newCommand ->
            currentNavigationCommand = newCommand
        }.launchIn(viewModelScope)

    private fun UserDashboardDataModel.observeDataState() {
        dataFlow.onEach { logoutState ->
            currentLogoutState = logoutState
            println("Logout State: $logoutState")
            if (logoutState is OpenDataModel.SuccessState<*>) {
                val logoutStateResponse = logoutState.data as? LoginResponse
                println("Logout State Response: $logoutStateResponse")

            }
        }.launchIn(viewModelScope)
    }

    fun login() {
        loginDataModel.login(
            currentWorkEmail.trim(),
            currentPassword.trim()
        )
    }

    fun logout() {
        loginDataModel.logoutUser()
    }

    fun resetAll(onComplete: () -> Unit = {}) {
        currentWorkEmail = ""
        currentPassword = ""
        currentNavigationCommand = null
        currentLoginState = OpenDataModel.EmptyState
        onComplete()
    }

}