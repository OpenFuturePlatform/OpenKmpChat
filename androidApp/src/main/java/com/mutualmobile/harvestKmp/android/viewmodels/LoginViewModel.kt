package com.mutualmobile.harvestKmp.android.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.mutualmobile.harvestKmp.datamodel.PraxisCommand
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel
import com.mutualmobile.harvestKmp.domain.model.response.GetUserResponse
import com.mutualmobile.harvestKmp.domain.model.response.LoginResponse
import com.mutualmobile.harvestKmp.features.datamodels.authApiDataModels.GetUserDataModel
import com.mutualmobile.harvestKmp.features.datamodels.authApiDataModels.LoginDataModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LoginViewModel : ViewModel() {
    var currentWorkEmail by mutableStateOf("")
    var currentPassword by mutableStateOf("")

    var currentNavigationCommand: PraxisCommand? by mutableStateOf(null)
    var currentLoginState: PraxisDataModel.DataState by mutableStateOf(PraxisDataModel.EmptyState)

    private val loginDataModel = LoginDataModel()
    private val getUserDataModel = GetUserDataModel()

    var currentErrorMsg: String? by mutableStateOf(null)

    init {
        with(loginDataModel) {
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
            if (loginState is PraxisDataModel.SuccessState<*>) {
                val loginResponse = loginState.data as? LoginResponse
                FirebaseMessaging.getInstance().token.addOnSuccessListener { fcmToken ->
                    println("LOGIN FcmToken: $fcmToken for userId: ${loginResponse?.userId!!}")
                    loginDataModel.saveFcmToken(fcmToken, loginResponse.userId!!)
                }
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
        currentLoginState = PraxisDataModel.EmptyState
        onComplete()
    }

}