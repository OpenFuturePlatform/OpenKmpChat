package com.mutualmobile.harvestKmp.android.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mutualmobile.harvestKmp.android.ui.utils.SecurityUtils
import com.mutualmobile.harvestKmp.datamodel.OpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import com.mutualmobile.harvestKmp.domain.model.response.LoginResponse
import com.mutualmobile.harvestKmp.features.datamodels.authApiDataModels.PinInputDataModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class PinInputViewModel : ViewModel() {
    var currentPinCode by mutableStateOf("")

    var currentNavigationCommand: OpenCommand? by mutableStateOf(null)
    var currentPinLockState: OpenDataModel.DataState by mutableStateOf(OpenDataModel.EmptyState)
    var currentPinCreateState: OpenDataModel.DataState by mutableStateOf(OpenDataModel.EmptyState)

    private val pinInputDataModel = PinInputDataModel()
    var isAuthenticated by mutableStateOf(false)
    var isPinSet by mutableStateOf(false)
    var currentErrorMsg: String? by mutableStateOf(null)

    init {
        with(pinInputDataModel) {
            observeDataState()
            observeNavigationCommands()
        }
    }

    private fun PinInputDataModel.observeNavigationCommands() =
        praxisCommand.onEach { newCommand ->
            currentNavigationCommand = newCommand
        }.launchIn(viewModelScope)

    private fun PinInputDataModel.observeDataState() {
        dataFlow.onEach { pinLockState ->
            currentPinLockState = pinLockState
            println("Pin lock state: $pinLockState")
            if (currentPinLockState is OpenDataModel.SuccessState<*>) {
                val loginResponse = (currentPinLockState as OpenDataModel.SuccessState<*>).data as LoginResponse
                isAuthenticated = loginResponse.token?.isNotEmpty() ?: false
                currentErrorMsg = loginResponse.message
            }
        }.launchIn(viewModelScope)
    }

    fun onPinCodeChanged(context: Context, newPinCode: String) {
        currentPinCode = newPinCode
        println("Pin code request: $newPinCode")
//        val encryptSecretWithPin = SecurityUtils.encryptSecretWithPin(newPinCode, SecurityUtils.knownSecret)
//        println("Pin code encrypted: $encryptSecretWithPin")
//        saveEncryptedSecret(context, SecurityUtils.encryptSecretWithPin(newPinCode, SecurityUtils.knownSecret))
//        println("Pin code restored: ${SecurityUtils.decryptSecretWithPin(newPinCode, getEncryptedSecret(context)!!)}")
        val encryptSecretWithPin = SecurityUtils.encryptWithPassword(SecurityUtils.knownSecret, newPinCode)
        println("Pin code encrypted: $encryptSecretWithPin")
        SecurityUtils.saveEncryptedSecret(context, encryptSecretWithPin)
        println("Pin code restored: ${SecurityUtils.decryptWithPassword(SecurityUtils.getEncryptedSecret(context)!!, newPinCode)}")
    }

    fun removePinCode(context: Context){
        SecurityUtils.deleteEncryptedSecret(context)
    }

    fun savePinCode(context: Context, pinCode: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userPin", pinCode)
        editor.apply()
    }
    fun getStoredPin(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userPin", null)
    }

    fun isPinSet(context: Context): Boolean {
        isPinSet = SecurityUtils.isPinSet(context)
        return isPinSet
    }

    fun checkPin(context: Context, pinCode: String): Boolean {
        println("Encrypted secret: ${SecurityUtils.getEncryptedSecret(context)}")
        println("Decrypted secret: ${SecurityUtils.decryptWithPassword(SecurityUtils.getEncryptedSecret(context)!!, pinCode)} with pin: $pinCode")
        return SecurityUtils.getEncryptedSecret(context) == SecurityUtils.encryptWithPassword(SecurityUtils.knownSecret, pinCode)
    }

    fun resetAll(onComplete: () -> Unit = {}) {
        currentPinCode = ""
        currentNavigationCommand = null
        currentPinLockState = OpenDataModel.EmptyState
        onComplete()
    }

}