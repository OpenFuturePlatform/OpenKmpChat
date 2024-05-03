package com.mutualmobile.harvestKmp.domain.usecases.groupApiUseCases

import com.mutualmobile.harvestKmp.data.network.chat.GroupApi
import com.mutualmobile.harvestKmp.domain.model.request.GroupCreateRequest
import com.mutualmobile.harvestKmp.domain.model.response.GroupCreateResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse

class CreateGroupUseCase(private val groupApi: GroupApi) {
    suspend operator fun invoke(request: GroupCreateRequest): NetworkResponse<GroupCreateResponse> {
        return groupApi.createGroup(request)
    }
}