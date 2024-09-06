package com.mutualmobile.harvestKmp.domain.usecases.userWalletUseCases

import com.mutualmobile.harvestKmp.data.network.wallet.WalletApi
import com.mutualmobile.harvestKmp.domain.model.request.SaveWalletRequest
import com.mutualmobile.harvestKmp.domain.model.response.WalletResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse

class SaveWalletUseCase(private val walletApi: WalletApi) {
    suspend operator fun invoke(saveWalletRequest: SaveWalletRequest): NetworkResponse<WalletResponse> {
        return walletApi.save(saveWalletRequest)
    }
}