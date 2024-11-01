package com.mutualmobile.harvestKmp.domain.usecases.stateApiUseCases

import com.mutualmobile.harvestKmp.data.network.state.StateApi
import com.mutualmobile.harvestKmp.domain.model.request.BalanceRequest
import com.mutualmobile.harvestKmp.domain.model.response.TransactionResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse

class GetTransactionsUseCase(private val stateApi: StateApi) {
    suspend operator fun invoke(request: BalanceRequest): NetworkResponse<List<TransactionResponse>> {
        return stateApi.getTransactions(request)
    }
}