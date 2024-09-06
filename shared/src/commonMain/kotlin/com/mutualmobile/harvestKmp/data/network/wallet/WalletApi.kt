package com.mutualmobile.harvestKmp.data.network.wallet

import com.mutualmobile.harvestKmp.domain.model.request.CreateWalletRequest
import com.mutualmobile.harvestKmp.domain.model.request.DecryptWalletRequest
import com.mutualmobile.harvestKmp.domain.model.request.SaveWalletRequest
import com.mutualmobile.harvestKmp.domain.model.response.WalletResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse

interface WalletApi {
    suspend fun get(username: String): NetworkResponse<List<WalletResponse>>
    suspend fun generate(createWalletRequest: CreateWalletRequest): NetworkResponse<WalletResponse>
    suspend fun decrypt(decryptWalletRequest: DecryptWalletRequest): NetworkResponse<String>
    suspend fun save(saveWalletRequest: SaveWalletRequest): NetworkResponse<WalletResponse>
}