package com.mutualmobile.harvestKmp.domain.model.response

import com.mutualmobile.harvestKmp.domain.model.TextType
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class ContactResponse(
    val id: String,
    val email: String,
    val phoneNumber: String?,
//    val registeredAt: LocalDateTime?,
//    val lastLoginAt: LocalDateTime?,
    val firstName: String?,
    val lastName: String?,
    val avatar: String?,
    val active: Boolean?
)
