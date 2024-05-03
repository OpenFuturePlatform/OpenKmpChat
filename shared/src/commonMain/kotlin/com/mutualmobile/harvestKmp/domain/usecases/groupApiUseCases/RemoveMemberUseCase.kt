package com.mutualmobile.harvestKmp.domain.usecases.groupApiUseCases

import com.mutualmobile.harvestKmp.data.network.chat.GroupApi
import com.mutualmobile.harvestKmp.domain.model.request.GroupCreateRequest
import com.mutualmobile.harvestKmp.domain.model.request.GroupMemberUpdateRequest
import com.mutualmobile.harvestKmp.domain.model.response.GroupCreateResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse
import io.ktor.client.statement.*

class RemoveMemberUseCase(private val groupApi: GroupApi) {
    suspend operator fun invoke(request: GroupMemberUpdateRequest): NetworkResponse<HttpResponse> {
        return groupApi.removeMember(request)
    }
}