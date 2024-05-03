package com.mutualmobile.harvestKmp.data.network.chat.impl

import com.mutualmobile.harvestKmp.data.network.Endpoint
import com.mutualmobile.harvestKmp.data.network.chat.ChatApi
import com.mutualmobile.harvestKmp.data.network.chat.UserApi
import com.mutualmobile.harvestKmp.data.network.getSafeNetworkResponse
import com.mutualmobile.harvestKmp.domain.model.request.UserDetailRequest
import com.mutualmobile.harvestKmp.domain.model.response.ContactResponse
import com.mutualmobile.harvestKmp.domain.model.response.UserDetailResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlin.collections.get

class UserApiImpl (
    private val httpClient: HttpClient
) : UserApi {
    override suspend fun getContacts(): NetworkResponse<List<ContactResponse>> =
        getSafeNetworkResponse {
            httpClient.get(
                urlString = "${Endpoint.SPRING_BOOT_BASE_URL}${Endpoint.LIST_CONTACTS}"
            ) {
                contentType(ContentType.Application.Json)
            }
        }

    override suspend fun getUserDetail(userDetailRequest: UserDetailRequest): NetworkResponse<UserDetailResponse> =
        getSafeNetworkResponse {
            httpClient.post(
                urlString = "${Endpoint.SPRING_BOOT_BASE_URL}${Endpoint.USER_DETAIL}"
            ) {
                contentType(ContentType.Application.Json)
                setBody(userDetailRequest)
            }
        }

}