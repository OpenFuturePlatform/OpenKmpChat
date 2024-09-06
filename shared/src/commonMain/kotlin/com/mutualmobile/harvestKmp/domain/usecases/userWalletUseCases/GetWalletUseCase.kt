package com.mutualmobile.harvestKmp.domain.usecases.userWalletUseCases

import com.mutualmobile.harvestKmp.data.network.wallet.WalletApi
import com.mutualmobile.harvestKmp.domain.model.response.WalletResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse

class GetWalletUseCase(private val walletApi: WalletApi) {
    suspend operator fun invoke(username: String): NetworkResponse<List<WalletResponse>> {
        return walletApi.get(username)
    }
}