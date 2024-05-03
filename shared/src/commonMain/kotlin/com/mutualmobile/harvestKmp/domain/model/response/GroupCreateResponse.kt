package com.mutualmobile.harvestKmp.domain.model.response

import kotlinx.serialization.Serializable

@Serializable
data class GroupCreateResponse(
    val id: Int,
    val name: String,
    val creator: String,
)
