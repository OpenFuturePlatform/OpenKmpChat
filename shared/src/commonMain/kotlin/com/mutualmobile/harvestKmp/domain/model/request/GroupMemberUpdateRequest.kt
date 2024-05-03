package com.mutualmobile.harvestKmp.domain.model.request

import kotlinx.serialization.Serializable

@Serializable
data class GroupMemberUpdateRequest(
    val groupId: String,
    val users: List<String>
)
