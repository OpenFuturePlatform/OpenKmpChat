package com.mutualmobile.harvestKmp.domain.usecases.stateApiUseCases

import com.mutualmobile.harvestKmp.data.network.state.StateApi
import com.mutualmobile.harvestKmp.domain.model.response.CoinGateRate
import com.mutualmobile.harvestKmp.features.NetworkResponse

class GetRatesUseCase(private val stateApi: StateApi) {
    suspend operator fun invoke(ticker: String?): NetworkResponse<CoinGateRate> {
        return stateApi.getPrices(ticker)
    }
}