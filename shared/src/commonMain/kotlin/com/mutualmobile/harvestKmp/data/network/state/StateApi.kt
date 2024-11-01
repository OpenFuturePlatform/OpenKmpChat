package com.mutualmobile.harvestKmp.data.network.state

import com.mutualmobile.harvestKmp.domain.model.request.BalanceRequest
import com.mutualmobile.harvestKmp.domain.model.request.BroadcastRequest
import com.mutualmobile.harvestKmp.domain.model.response.*
import com.mutualmobile.harvestKmp.features.NetworkResponse

interface StateApi {
    suspend fun getPrices(ticker: String?): NetworkResponse<CoinGateRate>
    suspend fun getPrice(ticker: String): NetworkResponse<ExchangeRate>
    suspend fun getBalance(request : BalanceRequest): NetworkResponse<WalletBalanceResponse>
    suspend fun getTransactions(request : BalanceRequest): NetworkResponse<List<TransactionResponse>>
    suspend fun getContracts(isTest : Boolean): NetworkResponse<List<ContractResponse>>
    suspend fun getGasLimit(request : BalanceRequest): NetworkResponse<Long>
    suspend fun getGasPrice(request : BalanceRequest): NetworkResponse<Long>
    suspend fun getNonce(request : BalanceRequest): NetworkResponse<Long>
    suspend fun broadcastTransaction(request : BroadcastRequest): NetworkResponse<String>
}