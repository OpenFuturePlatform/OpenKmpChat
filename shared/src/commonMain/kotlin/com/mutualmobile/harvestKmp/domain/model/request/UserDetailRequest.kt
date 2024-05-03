package com.mutualmobile.harvestKmp.domain.model.request

import kotlinx.serialization.Serializable

@Serializable
data class UserDetailRequest (
    val username: String,
    val email: String
)