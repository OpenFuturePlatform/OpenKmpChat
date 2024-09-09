package com.mutualmobile.harvestKmp.domain.usecases.orgApiUseCases

import com.mutualmobile.harvestKmp.data.network.org.OrgApi
import com.mutualmobile.harvestKmp.domain.model.request.OpenOrganization
import com.mutualmobile.harvestKmp.domain.model.response.ApiResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse

class FindOrgByIdUseCase(private val orgApi: OrgApi) {
    suspend operator fun invoke(orgId: String): NetworkResponse<ApiResponse<OpenOrganization>> {
        return orgApi.findOrgById(orgId = orgId)
    }
}