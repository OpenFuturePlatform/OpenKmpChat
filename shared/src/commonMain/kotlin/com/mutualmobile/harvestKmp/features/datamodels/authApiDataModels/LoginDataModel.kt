package com.mutualmobile.harvestKmp.features.datamodels.authApiDataModels

import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes
import com.mutualmobile.harvestKmp.datamodel.ModalOpenCommand
import com.mutualmobile.harvestKmp.datamodel.NavigationOpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import com.mutualmobile.harvestKmp.di.AuthApiUseCaseComponent
import com.mutualmobile.harvestKmp.di.SharedComponent
import com.mutualmobile.harvestKmp.di.UseCasesComponent
import com.mutualmobile.harvestKmp.domain.model.request.FcmToken
import com.mutualmobile.harvestKmp.domain.model.response.LoginResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class LoginDataModel :
    OpenDataModel(), KoinComponent {

    private val _dataFlow = MutableSharedFlow<DataState>()
    val dataFlow = _dataFlow.asSharedFlow()

    private val useCasesComponent = UseCasesComponent()
    private val authApiUseCasesComponent = AuthApiUseCaseComponent()
    private val tokenLocal = SharedComponent().provideTokenLocal()

    private val fcmUseCase = authApiUseCasesComponent.provideFcmTokenUseCase()
    private val loginUseCase = authApiUseCasesComponent.provideLoginUseCase()
    private val saveSettingsUseCase = useCasesComponent.provideSaveSettingsUseCase()
    private val provideLogoutUseCase = authApiUseCasesComponent.provideLogoutUseCase()

    override fun activate() {
    }

    override fun destroy() {
        dataModelScope.cancel()
    }

    override fun refresh() {

    }

    fun login(email: String, password: String) {
        dataModelScope.launch(exceptionHandler) {
            _dataFlow.emit(LoadingState)
            when (val loginResponse = loginUseCase(
                email = email,
                password = password
            )) {
                is NetworkResponse.Success -> {
                    _dataFlow.emit(SuccessState(loginResponse.data))
                    saveToken(loginResponse)
                    intOpenCommand.emit(
                        NavigationOpenCommand(
                            screen = HarvestRoutes.Screen.CHAT
                        )
                    )
                }

                is NetworkResponse.Failure -> {
                    _dataFlow.emit(ErrorState(loginResponse.throwable))
                    intOpenCommand.emit(
                        ModalOpenCommand(
                            title = "Error",
                            loginResponse.throwable.message ?: "An Unknown error has happened"
                        )
                    )
                }

                is NetworkResponse.Unauthorized -> {
                    _dataFlow.emit(ErrorState(loginResponse.throwable))
                    intOpenCommand.emit(
                        ModalOpenCommand(
                            title = "Error",
                            loginResponse.throwable.message ?: "An Unknown error has happened"
                        )
                    )
                }
            }
        }
    }

    private fun saveToken(
        loginResponse: NetworkResponse.Success<LoginResponse>
    ) {
        loginResponse.data.token?.let { token ->
            loginResponse.data.refreshToken?.let { refreshToken ->
                saveSettingsUseCase(
                    token,
                    refreshToken
                )
            }
        }
    }

    fun saveFcmToken(
        fcmToken: String,
        userId: String
    ) {
        dataModelScope.launch(exceptionHandler) {
            when (val response = fcmUseCase(FcmToken(token = fcmToken, userId = userId))) {
                is NetworkResponse.Success -> {
                    println("fcm save response ${response.data.data}")
                }

                is NetworkResponse.Failure -> {
                }

                is NetworkResponse.Unauthorized -> {
                }
            }
        }
    }

    fun logoutUser() {
        dataModelScope.launch {
            intOpenCommand.emit(
                ModalOpenCommand(
                    title = "Work in Progress",
                    message = "The mobile client app is currently made for organization users only, if you're an Admin or a SuperAdmin, you can click on OK and go to the Harvest Web App which supports Admin sign-in"
                )
            )
            provideLogoutUseCase()
        }
    }
}
