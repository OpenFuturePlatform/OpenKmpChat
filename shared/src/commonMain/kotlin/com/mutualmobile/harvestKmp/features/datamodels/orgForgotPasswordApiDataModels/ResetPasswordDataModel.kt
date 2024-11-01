package com.mutualmobile.harvestKmp.features.datamodels.orgForgotPasswordApiDataModels

import com.mutualmobile.harvestKmp.datamodel.ModalOpenCommand
import com.mutualmobile.harvestKmp.datamodel.NavigationOpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import com.mutualmobile.harvestKmp.di.ForgotPasswordApiUseCaseComponent
import com.mutualmobile.harvestKmp.domain.model.request.ResetPasswordRequest
import com.mutualmobile.harvestKmp.domain.model.response.ApiResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.core.component.KoinComponent

class ResetPasswordDataModel() :
    OpenDataModel(), KoinComponent {
  private val _dataFlow = MutableSharedFlow<DataState>()
    val dataFlow = _dataFlow.asSharedFlow()

    private var currentLoadingJob: Job? = null
    private val forgotPasswordApiUseCaseComponent = ForgotPasswordApiUseCaseComponent()
    private val resetPasswordUseCase =
        forgotPasswordApiUseCaseComponent.provideResetPasswordUseCase()

    override fun activate() {
    }

    override fun destroy() {
        dataModelScope.cancel()
    }

    override fun refresh() {

    }

    fun resetPassword(password: String, token: String) {
        currentLoadingJob?.cancel()
        currentLoadingJob = dataModelScope.launch {
            _dataFlow.emit(LoadingState)
            when (val changePasswordResponse =
                resetPasswordUseCase(
                    ResetPasswordRequest(
                        password = password,
                        token = token
                    )
                )) {
                is NetworkResponse.Success<*> -> {
                    if (changePasswordResponse.data is ApiResponse<*>) {
                        intOpenCommand.emit(
                            ModalOpenCommand(
                                "Response",
                                changePasswordResponse.data.message ?: "Woah!"
                            )
                        )
                    }
                    _dataFlow.emit(SuccessState(changePasswordResponse.data))
                    intOpenCommand.emit(NavigationOpenCommand(""))
                }
                is NetworkResponse.Failure -> {
                    _dataFlow.emit(ErrorState(changePasswordResponse.throwable))
                }
                else -> {}
            }
        }
    }
}