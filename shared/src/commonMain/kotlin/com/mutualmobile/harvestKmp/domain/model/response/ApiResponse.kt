package com.mutualmobile.harvestKmp.domain.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val message: String? = null,
    val data: T? = null,
)

@Serializable
data class ApiError(
    val reason: String? = null,
    val status: Int? = null
)