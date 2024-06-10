package com.mutualmobile.harvestKmp.domain.usecases.chatApiUseCases

import com.mutualmobile.harvestKmp.data.network.attachment.AttachmentApi
import com.mutualmobile.harvestKmp.domain.model.Attachment
import com.mutualmobile.harvestKmp.features.NetworkResponse

class DownloadAttachmentUseCase(private val attachmentApi: AttachmentApi) {
    suspend operator fun invoke(id: Int): NetworkResponse<ByteArray> {
        return attachmentApi.downloadFile(id)
    }
}