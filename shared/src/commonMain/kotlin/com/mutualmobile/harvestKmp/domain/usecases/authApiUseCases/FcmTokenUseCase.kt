package com.mutualmobile.harvestKmp.domain.usecases.authApiUseCases

import com.mutualmobile.harvestKmp.data.network.authUser.AuthApi
import com.mutualmobile.harvestKmp.domain.model.request.FcmToken
import com.mutualmobile.harvestKmp.domain.model.request.User
import com.mutualmobile.harvestKmp.domain.model.response.ApiResponse
import com.mutualmobile.harvestKmp.domain.model.response.FcmTokenResponse
import com.mutualmobile.harvestKmp.domain.model.response.LoginResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse

class FcmTokenUseCase(private val authApi: AuthApi) {
    suspend operator fun invoke(fcmToken: FcmToken): NetworkResponse<ApiResponse<FcmTokenResponse>> {
        return authApi.fcmToken(fcmToken)
    }
}