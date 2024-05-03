package com.mutualmobile.harvestKmp.domain.usecases.groupApiUseCases

import com.mutualmobile.harvestKmp.data.network.chat.GroupApi
import com.mutualmobile.harvestKmp.domain.model.response.GroupDetailResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse

class GetGroupUseCase(private val groupApi: GroupApi) {
    suspend operator fun invoke(groupId: String): NetworkResponse<GroupDetailResponse> {
        return groupApi.getGroup(groupId)
    }
}