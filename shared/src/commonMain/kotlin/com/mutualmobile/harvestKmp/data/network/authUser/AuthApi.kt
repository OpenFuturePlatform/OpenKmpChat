package com.mutualmobile.harvestKmp.data.network.authUser

import com.mutualmobile.harvestKmp.domain.model.request.FcmToken
import com.mutualmobile.harvestKmp.domain.model.request.OpenOrganization
import com.mutualmobile.harvestKmp.domain.model.request.OpenUser
import com.mutualmobile.harvestKmp.domain.model.request.User
import com.mutualmobile.harvestKmp.domain.model.response.ApiResponse
import com.mutualmobile.harvestKmp.domain.model.response.FcmTokenResponse
import com.mutualmobile.harvestKmp.domain.model.response.GetUserResponse
import com.mutualmobile.harvestKmp.domain.model.response.LoginResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse

interface AuthApi {

    suspend fun fcmToken(fcmToken: FcmToken): NetworkResponse<ApiResponse<FcmTokenResponse>>

    suspend fun existingOrgSignUp(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): NetworkResponse<ApiResponse<OpenUser>>

    suspend fun newOrgSignUp(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): NetworkResponse<ApiResponse<OpenUser>>

    suspend fun logout(): NetworkResponse<ApiResponse<String>>

    suspend fun refreshToken(refreshToken: String): LoginResponse

    suspend fun getUser(): NetworkResponse<GetUserResponse>

    suspend fun changePassword(
        password: String,
        oldPassword: String
    ): NetworkResponse<ApiResponse<OpenOrganization>>

    suspend fun login(email: String, password: String): NetworkResponse<LoginResponse>

    suspend fun updateUser(id: String): User

}