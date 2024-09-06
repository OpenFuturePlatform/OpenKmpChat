package com.mutualmobile.harvestKmp.data.network.wallet.impl

import com.mutualmobile.harvestKmp.data.network.Endpoint
import com.mutualmobile.harvestKmp.data.network.wallet.WalletApi
import com.mutualmobile.harvestKmp.data.network.getSafeNetworkResponse
import com.mutualmobile.harvestKmp.domain.model.request.CreateWalletRequest
import com.mutualmobile.harvestKmp.domain.model.request.DecryptWalletRequest
import com.mutualmobile.harvestKmp.domain.model.request.SaveWalletRequest
import com.mutualmobile.harvestKmp.domain.model.response.WalletResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class WalletApiImpl(
    private val httpClient: HttpClient
) : WalletApi {

    override suspend fun get(username: String): NetworkResponse<List<WalletResponse>> =
        getSafeNetworkResponse {
            httpClient.get(
                urlString = "${Endpoint.SPRING_BOOT_BASE_URL}${Endpoint.WALLET_URL}"
            ) {
                contentType(ContentType.Application.Json)
            }
        }

    override suspend fun generate(createWalletRequest: CreateWalletRequest): NetworkResponse<WalletResponse> = getSafeNetworkResponse {
        httpClient.post(
            urlString = "${Endpoint.SPRING_BOOT_BASE_URL}${Endpoint.WALLET_URL}/generate"
        ) {
            contentType(ContentType.Application.Json)
            setBody(createWalletRequest)
        }
    }

    override suspend fun decrypt(decryptWalletRequest: DecryptWalletRequest): NetworkResponse<String> = getSafeNetworkResponse {
        httpClient.post(
            urlString = "${Endpoint.SPRING_BOOT_BASE_URL}${Endpoint.WALLET_URL}/decrypt"
        ) {
            contentType(ContentType.Application.Json)
            setBody(decryptWalletRequest)
        }
    }

    override suspend fun save(saveWalletRequest: SaveWalletRequest): NetworkResponse<WalletResponse> = getSafeNetworkResponse {
        println("save reqyest $saveWalletRequest")
        httpClient.post(
            urlString = "${Endpoint.SPRING_BOOT_BASE_URL}${Endpoint.WALLET_URL}/save"
        ) {
            contentType(ContentType.Application.Json)
            setBody(saveWalletRequest)
        }
    }
}