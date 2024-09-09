package com.mutualmobile.harvestKmp.domain.model.request

import kotlinx.serialization.Serializable

@Serializable
data class FcmToken(
    val userId: String? = null,
    val token: String? = null
)