package com.mutualmobile.harvestKmp.data.network.state

import com.mutualmobile.harvestKmp.domain.model.request.BalanceRequest
import com.mutualmobile.harvestKmp.domain.model.response.CoinGateRate
import com.mutualmobile.harvestKmp.domain.model.response.WalletBalanceResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse

interface StateApi {
    suspend fun getPrices(ticker: String?): NetworkResponse<CoinGateRate>
    suspend fun getBalance(request : BalanceRequest): NetworkResponse<WalletBalanceResponse>
}