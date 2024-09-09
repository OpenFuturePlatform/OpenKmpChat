package com.mutualmobile.harvestKmp.data.network.org

import com.mutualmobile.harvestKmp.domain.model.request.OpenOrganization
import com.mutualmobile.harvestKmp.domain.model.response.ApiResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse

interface OrgApi {

    suspend fun findOrgByIdentifier(identifier: String): NetworkResponse<ApiResponse<OpenOrganization>>
    suspend fun findOrgById(orgId: String): NetworkResponse<ApiResponse<OpenOrganization>>

}