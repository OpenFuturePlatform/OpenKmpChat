package com.mutualmobile.harvestKmp.data.network.attachment

import com.mutualmobile.harvestKmp.features.NetworkResponse

interface AttachmentApi {
    suspend fun uploadFile(imageBytes: ByteArray, fileName: String): NetworkResponse<String>
}