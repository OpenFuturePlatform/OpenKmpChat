package com.mutualmobile.harvestKmp.data.network.state.impl

import com.mutualmobile.harvestKmp.data.network.Endpoint
import com.mutualmobile.harvestKmp.data.network.getSafeNetworkResponse
import com.mutualmobile.harvestKmp.data.network.state.StateApi
import com.mutualmobile.harvestKmp.domain.model.request.BalanceRequest
import com.mutualmobile.harvestKmp.domain.model.response.CoinGateRate
import com.mutualmobile.harvestKmp.domain.model.response.ContractResponse
import com.mutualmobile.harvestKmp.domain.model.response.WalletBalanceResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class StateApiImpl(
    private val httpClient: HttpClient
) : StateApi {

    override suspend fun getPrices(ticker: String?): NetworkResponse<CoinGateRate> =
        getSafeNetworkResponse {
            httpClient.get(
                urlString = "${Endpoint.STATE_URL}${Endpoint.RATES_URL}"
            ) {
                contentType(ContentType.Application.Json)
            }
        }

    override suspend fun getBalance(request: BalanceRequest): NetworkResponse<WalletBalanceResponse> =
        getSafeNetworkResponse {
            httpClient.post(
                urlString = "${Endpoint.STATE_URL}${Endpoint.BALANCE_URL}"
            ) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }

    override suspend fun getContracts(isTest: Boolean): NetworkResponse<List<ContractResponse>> =
        getSafeNetworkResponse {
            httpClient.post(
                urlString = "${Endpoint.STATE_URL}${Endpoint.CONTRACT_URL}?isTest=$isTest"
            ) {
                contentType(ContentType.Application.Json)
            }
        }

    override suspend fun getGasLimit(request: BalanceRequest): NetworkResponse<Long> =
        getSafeNetworkResponse {
            httpClient.post(
                urlString = "${Endpoint.STATE_URL}${Endpoint.GAS_LIMIT_URL}"
            ) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }

    override suspend fun getGasPrice(request: BalanceRequest): NetworkResponse<Long> =
        getSafeNetworkResponse {
            httpClient.post(
                urlString = "${Endpoint.STATE_URL}${Endpoint.GAS_PRICE_URL}"
            ) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }

    override suspend fun getNonce(request: BalanceRequest): NetworkResponse<Long> =
        getSafeNetworkResponse {
            httpClient.post(
                urlString = "${Endpoint.STATE_URL}${Endpoint.NONCE_URL}"
            ) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }
}