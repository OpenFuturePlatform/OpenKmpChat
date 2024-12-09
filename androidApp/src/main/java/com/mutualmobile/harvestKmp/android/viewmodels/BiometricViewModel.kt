package com.mutualmobile.harvestKmp.android.viewmodels

import android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG
import android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class BiometricViewModel : ViewModel() {

    var isAuthenticated = mutableStateOf(false)

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var biometricManager: BiometricManager
    fun setBiometricPrompt(prompt: BiometricPrompt) { biometricPrompt = prompt }
    fun setBiometricManager(manager: BiometricManager) { biometricManager = manager }
    fun isBiometricAvailable(): Boolean { return biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS }
    fun authenticate() {
        val authenticators = if (Build.VERSION.SDK_INT >= 30) {
            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
        } else {
            BIOMETRIC_STRONG
        }
        biometricPrompt.authenticate(
            BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Log in using your fingerprint")
                .setNegativeButtonText("Cancel")
                .build()
        )
    }
}