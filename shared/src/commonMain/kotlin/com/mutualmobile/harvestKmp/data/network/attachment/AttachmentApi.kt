package com.mutualmobile.harvestKmp.data.network.attachment

import com.mutualmobile.harvestKmp.domain.model.Attachment
import com.mutualmobile.harvestKmp.features.NetworkResponse

interface AttachmentApi {
    suspend fun uploadFile(attachment: Attachment): NetworkResponse<String>
    suspend fun uploadFile(imageBytes: ByteArray, fileName: String, captionText: String): NetworkResponse<String>
}