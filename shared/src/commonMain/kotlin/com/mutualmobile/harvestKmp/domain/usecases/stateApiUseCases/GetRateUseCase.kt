package com.mutualmobile.harvestKmp.domain.usecases.stateApiUseCases

import com.mutualmobile.harvestKmp.data.network.state.StateApi
import com.mutualmobile.harvestKmp.domain.model.response.ExchangeRate
import com.mutualmobile.harvestKmp.features.NetworkResponse

class GetRateUseCase(private val stateApi: StateApi) {
    suspend operator fun invoke(ticker: String): NetworkResponse<ExchangeRate> {
        return stateApi.getPrice(ticker)
    }
}