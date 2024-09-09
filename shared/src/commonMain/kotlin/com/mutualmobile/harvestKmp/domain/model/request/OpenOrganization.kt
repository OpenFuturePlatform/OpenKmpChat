package com.mutualmobile.harvestKmp.domain.model.request

import kotlinx.serialization.Serializable

@Serializable
data class OpenOrganization(
    val id: String? = null,
    val imgUrl: String? = null,
    val name: String? = null,
    val website: String? = null,
    val identifier: String? = null
)

@Serializable
data class OpenUser(
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null
)