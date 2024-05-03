package com.mutualmobile.harvestKmp.domain.model.request

import kotlinx.serialization.Serializable

@Serializable
data class GroupCreateRequest(
    val name: String,
    val creator: String,
    val participants: List<String>
)
