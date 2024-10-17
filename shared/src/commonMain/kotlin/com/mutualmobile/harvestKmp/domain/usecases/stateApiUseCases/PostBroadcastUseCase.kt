package com.mutualmobile.harvestKmp.domain.usecases.stateApiUseCases

import com.mutualmobile.harvestKmp.data.network.state.StateApi
import com.mutualmobile.harvestKmp.domain.model.request.BroadcastRequest
import com.mutualmobile.harvestKmp.features.NetworkResponse

class PostBroadcastUseCase(private val stateApi: StateApi) {
    suspend operator fun invoke(request: BroadcastRequest): NetworkResponse<String> {
        println("Broadcasting transaction: $request")
        return stateApi.broadcastTransaction(request)
    }
}