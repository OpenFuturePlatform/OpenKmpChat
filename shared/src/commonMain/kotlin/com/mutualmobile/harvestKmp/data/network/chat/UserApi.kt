package com.mutualmobile.harvestKmp.data.network.chat

import com.mutualmobile.harvestKmp.domain.model.request.UserDetailRequest
import com.mutualmobile.harvestKmp.domain.model.response.ContactResponse
import com.mutualmobile.harvestKmp.domain.model.response.UserDetailResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse

interface UserApi {
    suspend fun getContacts(): NetworkResponse<List<ContactResponse>>
    suspend fun getUserDetail(userDetailRequest: UserDetailRequest): NetworkResponse<UserDetailResponse>
}