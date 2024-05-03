package com.mutualmobile.harvestKmp.domain.usecases.chatApiUseCases

import com.mutualmobile.harvestKmp.data.network.attachment.AttachmentApi
import com.mutualmobile.harvestKmp.features.NetworkResponse

class UploadAttachmentUseCase(private val attachmentApi: AttachmentApi) {
    suspend operator fun invoke(imageBytes: ByteArray, fileName: String): NetworkResponse<String> {
        return attachmentApi.uploadFile(imageBytes, fileName)
    }
}