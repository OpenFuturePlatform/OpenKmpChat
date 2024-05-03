package com.mutualmobile.harvestKmp.domain.usecases.chatApiUseCases

import com.mutualmobile.harvestKmp.data.network.chat.UserApi
import com.mutualmobile.harvestKmp.domain.model.response.ContactResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse

class GetAllContactsUseCase(
    private val userApi: UserApi
) {
    suspend operator fun invoke(): NetworkResponse<List<ContactResponse>> {
        return userApi.getContacts()
    }
}