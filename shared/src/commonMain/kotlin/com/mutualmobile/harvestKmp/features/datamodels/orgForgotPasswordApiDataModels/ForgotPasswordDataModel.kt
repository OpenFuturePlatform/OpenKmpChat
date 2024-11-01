package com.mutualmobile.harvestKmp.features.datamodels.orgForgotPasswordApiDataModels

import com.mutualmobile.harvestKmp.datamodel.*
import com.mutualmobile.harvestKmp.di.ForgotPasswordApiUseCaseComponent
import com.mutualmobile.harvestKmp.features.NetworkResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.core.component.KoinComponent

class ForgotPasswordDataModel() :
    OpenDataModel(), KoinComponent {
  private val _dataFlow = MutableSharedFlow<DataState>()
    val dataFlow = _dataFlow.asSharedFlow()
    private var currentLoadingJob: Job? = null
    private val forgotPasswordApiUseCaseComponent = ForgotPasswordApiUseCaseComponent()
    private val forgotPasswordUseCase =
        forgotPasswordApiUseCaseComponent.provideForgotPasswordUseCase()

    fun forgotPassword(email: String) {
        currentLoadingJob?.cancel()
        currentLoadingJob =
            dataModelScope.launch(exceptionHandler) {
                _dataFlow.emit(LoadingState)
                when (val response = forgotPasswordUseCase(
                    email = email
                )) {
                    is NetworkResponse.Success -> {
                        intOpenCommand.emit(
                            ModalOpenCommand(
                                "Response",
                                response.data.message ?: "Woah!"
                            )
                        )
                        _dataFlow.emit(SuccessState(response.data))
                        println("SUCCESS, ${response.data.message}")
                    }
                    is NetworkResponse.Failure -> {
                        _dataFlow.emit(ErrorState(response.throwable))
                        println("FAILED, ${response.throwable.message}")
                    }
                    is NetworkResponse.Unauthorized -> {
                        settings.clear()
                        intOpenCommand.emit(ModalOpenCommand("Unauthorized", "Please login again!"))
                        intOpenCommand.emit(NavigationOpenCommand(""))
                    }
                }
            }
    }

    override fun activate() {
    }

    override fun destroy() {
        dataModelScope.cancel()
    }

    override fun refresh() {
    }
}