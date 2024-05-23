package com.mutualmobile.harvestKmp.domain.usecases.chatApiUseCases

import com.mutualmobile.harvestKmp.data.network.attachment.AttachmentApi
import com.mutualmobile.harvestKmp.domain.model.Attachment
import com.mutualmobile.harvestKmp.features.NetworkResponse

class UploadAttachmentUseCase(private val attachmentApi: AttachmentApi) {
//    suspend operator fun invoke(attachment: Attachment): NetworkResponse<String> {
//        return attachmentApi.uploadFile(attachment)
//        //eturn attachmentApi.uploadFile(attachment.fileByteArray, attachment.fileName, attachment.captionText)
//    }

    suspend operator fun invoke(imageBytes: ByteArray, fileName: String, captionText: String): NetworkResponse<String> {
        return attachmentApi.uploadFile(imageBytes, fileName, captionText)
    }
}