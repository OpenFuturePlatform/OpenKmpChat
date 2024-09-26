package com.mutualmobile.harvestKmp.domain.usecases.stateApiUseCases

import com.mutualmobile.harvestKmp.data.network.state.StateApi
import com.mutualmobile.harvestKmp.domain.model.response.ContractResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse

class GetContractsUseCase(private val stateApi: StateApi) {
    suspend operator fun invoke(request: Boolean): NetworkResponse<List<ContractResponse>> {
        return stateApi.getContracts(request)
    }
}