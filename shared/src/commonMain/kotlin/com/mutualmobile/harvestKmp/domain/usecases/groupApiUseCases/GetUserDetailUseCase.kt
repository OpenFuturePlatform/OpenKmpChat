package com.mutualmobile.harvestKmp.domain.usecases.groupApiUseCases

import com.mutualmobile.harvestKmp.data.network.chat.UserApi
import com.mutualmobile.harvestKmp.domain.model.request.UserDetailRequest
import com.mutualmobile.harvestKmp.domain.model.response.UserDetailResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse

class GetUserDetailUseCase(private val userApi: UserApi) {
    suspend operator fun invoke(username: String, email: String): NetworkResponse<UserDetailResponse> {
        return userApi.getUserDetail(UserDetailRequest(username, email))
    }
}