package com.mutualmobile.harvestKmp.domain.usecases.userWalletUseCases

import com.mutualmobile.harvestKmp.data.network.chat.WalletApi
import com.mutualmobile.harvestKmp.domain.model.request.DecryptWalletRequest
import com.mutualmobile.harvestKmp.features.NetworkResponse

class DecryptWalletUseCase(private val walletApi: WalletApi) {
    suspend operator fun invoke(decryptWalletRequest: DecryptWalletRequest): NetworkResponse<String> {
        return walletApi.decrypt(decryptWalletRequest)
    }
}