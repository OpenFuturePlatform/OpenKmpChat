package com.mutualmobile.harvestKmp.data.network.authUser

import com.mutualmobile.harvestKmp.domain.model.request.OpenOrganization
import com.mutualmobile.harvestKmp.domain.model.request.ResetPasswordRequest
import com.mutualmobile.harvestKmp.domain.model.response.ApiResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse

interface UserForgotPasswordApi {

    suspend fun resetPassword(
        resetPasswordRequest: ResetPasswordRequest
    ): NetworkResponse<ApiResponse<Unit>>

    suspend fun forgotPassword(email: String): NetworkResponse<ApiResponse<OpenOrganization>>

}