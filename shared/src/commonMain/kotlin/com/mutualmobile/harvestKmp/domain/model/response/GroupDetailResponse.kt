package com.mutualmobile.harvestKmp.domain.model.response

import kotlinx.serialization.Serializable

@Serializable
data class GroupDetailResponse(
    val id: Int,
    val name: String,
    val creator: String,
    val avatar: String,
    val participants: List<String>
)
