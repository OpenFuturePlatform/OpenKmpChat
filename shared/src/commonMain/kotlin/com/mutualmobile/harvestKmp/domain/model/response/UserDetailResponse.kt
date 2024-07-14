package com.mutualmobile.harvestKmp.domain.model.response

import kotlinx.serialization.Serializable

@Serializable
data class UserDetailResponse(
    val email: String?,
    val fullName: String?,
    val groups: List<UserGroupDetailResponse>
)

@Serializable
data class UserGroupDetailResponse(
    val id: String,
    val name: String
)