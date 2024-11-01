package com.mutualmobile.harvestKmp.android.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.mutualmobile.harvestKmp.android.ui.utils.showToast
import com.mutualmobile.harvestKmp.datamodel.OpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import com.mutualmobile.harvestKmp.features.datamodels.authApiDataModels.ChangePasswordDataModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ChangePasswordViewModel : ViewModel() {
    var changePasswordState: OpenDataModel.DataState by mutableStateOf(OpenDataModel.EmptyState)

    var changePasswordOpenCommand: OpenCommand? by mutableStateOf(null)

    private val changePasswordDataModel = ChangePasswordDataModel()

    var oldPassword by mutableStateOf("")
    var newPassword by mutableStateOf("")

    init {
        with(changePasswordDataModel) {
            observeNavigationCommands()
        }
    }

    private fun ChangePasswordDataModel.observeNavigationCommands() =
        praxisCommand.onEach { newCommand ->
            changePasswordOpenCommand = newCommand
        }.launchIn(viewModelScope)

    fun changePassword(ctx: Context, navController: NavHostController) {
        changePasswordDataModel.changePassWord(
            newPassword.trim(),
            oldPassword.trim(),
        ).onEach { passwordState ->
            changePasswordState = passwordState
            when (passwordState) {
                is OpenDataModel.SuccessState<*> -> {
                    ctx.showToast("Change password successful!")
                    resetAll {
                        navController.navigateUp()
                    }
                }
                else -> Unit
            }
        }.launchIn(viewModelScope)
    }

    fun resetAll(onComplete: () -> Unit = {}) {
        changePasswordState = OpenDataModel.EmptyState
        changePasswordOpenCommand = null
        onComplete()
    }
}