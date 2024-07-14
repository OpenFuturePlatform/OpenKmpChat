package com.mutualmobile.harvestKmp.domain.usecases.userWalletUseCases

import com.mutualmobile.harvestKmp.data.network.chat.WalletApi
import com.mutualmobile.harvestKmp.domain.model.request.CreateWalletRequest
import com.mutualmobile.harvestKmp.domain.model.response.WalletResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse

class GenerateWalletUseCase(private val walletApi: WalletApi) {
    suspend operator fun invoke(createWalletRequest: CreateWalletRequest): NetworkResponse<WalletResponse> {
        return walletApi.generate(createWalletRequest)
    }
}