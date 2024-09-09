package com.mutualmobile.harvestKmp.domain.usecases.authApiUseCases

import com.mutualmobile.harvestKmp.data.network.authUser.AuthApi
import com.mutualmobile.harvestKmp.domain.model.request.OpenUser
import com.mutualmobile.harvestKmp.domain.model.response.ApiResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse
import com.mutualmobile.harvestKmp.validators.SignUpFormValidator

class NewOrgSignUpUseCase(private val authApi: AuthApi) {
    suspend operator fun invoke(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): NetworkResponse<ApiResponse<OpenUser>> {
        try {
            SignUpFormValidator()(
                firstName = firstName,
                lastName = lastName,
                email = email,
                password = password
            )
            return authApi.newOrgSignUp(
                firstName = firstName,
                lastName = lastName,
                email = email,
                password = password
            )
        } catch (e: Throwable) {
            return NetworkResponse.Failure(e)
        }
    }
}