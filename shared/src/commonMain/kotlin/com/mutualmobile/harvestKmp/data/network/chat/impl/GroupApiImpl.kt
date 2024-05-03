package com.mutualmobile.harvestKmp.data.network.chat.impl

import com.mutualmobile.harvestKmp.data.network.Endpoint
import com.mutualmobile.harvestKmp.data.network.chat.GroupApi
import com.mutualmobile.harvestKmp.data.network.getSafeNetworkResponse
import com.mutualmobile.harvestKmp.domain.model.request.GroupCreateRequest
import com.mutualmobile.harvestKmp.domain.model.request.GroupMemberUpdateRequest
import com.mutualmobile.harvestKmp.domain.model.response.GroupCreateResponse
import com.mutualmobile.harvestKmp.domain.model.response.GroupDetailResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class GroupApiImpl(
    private val httpClient: HttpClient
) : GroupApi {
    override suspend fun createGroup(request: GroupCreateRequest): NetworkResponse<GroupCreateResponse> =
        getSafeNetworkResponse {
            httpClient.post(
                urlString = "${Endpoint.SPRING_BOOT_BASE_URL}${Endpoint.GROUP_URL}"
            ) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }

    override suspend fun getGroup(groupId: String): NetworkResponse<GroupDetailResponse> =
        getSafeNetworkResponse {
            httpClient.get(
                urlString = "${Endpoint.SPRING_BOOT_BASE_URL}${Endpoint.GROUP_URL}/$groupId"
            ) {
                contentType(ContentType.Application.Json)
            }
        }

    override suspend fun addMember(request: GroupMemberUpdateRequest): NetworkResponse<HttpResponse> =
        getSafeNetworkResponse {
            httpClient.put(
                urlString = "${Endpoint.SPRING_BOOT_BASE_URL}${Endpoint.GROUP_URL}/participants/add"
            ) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }

    override suspend fun removeMember(request: GroupMemberUpdateRequest): NetworkResponse<HttpResponse> =
        getSafeNetworkResponse {
            httpClient.put(
                urlString = "${Endpoint.SPRING_BOOT_BASE_URL}${Endpoint.GROUP_URL}/participants/remove"
            ) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }

}