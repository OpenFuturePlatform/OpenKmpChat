package com.mutualmobile.harvestKmp.domain.usecases.stateApiUseCases

import com.mutualmobile.harvestKmp.data.network.state.StateApi
import com.mutualmobile.harvestKmp.domain.model.request.BalanceRequest
import com.mutualmobile.harvestKmp.domain.model.response.WalletBalanceResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse

class GetGasLimitUseCase(private val stateApi: StateApi) {
    suspend operator fun invoke(request: BalanceRequest): NetworkResponse<Long> {
        return stateApi.getGasLimit(request)
    }
}