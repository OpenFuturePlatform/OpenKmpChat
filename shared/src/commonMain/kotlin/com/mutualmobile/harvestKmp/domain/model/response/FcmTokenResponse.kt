package com.mutualmobile.harvestKmp.domain.model.response

import com.mutualmobile.harvestKmp.utils.now
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class FcmTokenResponse(
    var id: Long? = null,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    var userId: String? = null,
    var firebaseToken: String? = null,
)
