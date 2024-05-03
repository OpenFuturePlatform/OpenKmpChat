package com.mutualmobile.harvestKmp.data.network.chat

import com.mutualmobile.harvestKmp.domain.model.request.GroupCreateRequest
import com.mutualmobile.harvestKmp.domain.model.request.GroupMemberUpdateRequest
import com.mutualmobile.harvestKmp.domain.model.response.GroupCreateResponse
import com.mutualmobile.harvestKmp.domain.model.response.GroupDetailResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse
import io.ktor.client.statement.*

interface GroupApi {
    suspend fun createGroup(request: GroupCreateRequest): NetworkResponse<GroupCreateResponse>
    suspend fun getGroup(groupId: String): NetworkResponse<GroupDetailResponse>
    suspend fun addMember(request: GroupMemberUpdateRequest): NetworkResponse<HttpResponse>
    suspend fun removeMember(request: GroupMemberUpdateRequest): NetworkResponse<HttpResponse>
}