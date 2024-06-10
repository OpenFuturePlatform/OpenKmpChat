package com.mutualmobile.harvestKmp.data.network.attachment.impl

import com.mutualmobile.harvestKmp.data.network.Endpoint
import com.mutualmobile.harvestKmp.data.network.attachment.AttachmentApi
import com.mutualmobile.harvestKmp.data.network.getSafeNetworkResponse
import com.mutualmobile.harvestKmp.domain.model.Attachment
import com.mutualmobile.harvestKmp.features.NetworkResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

class AttachmentApiImpl(
    private val httpClient: HttpClient
) : AttachmentApi {
    override suspend fun uploadFile(attachment: Attachment): NetworkResponse<String> =
        getSafeNetworkResponse {

            httpClient.post(
                urlString = "${Endpoint.SPRING_BOOT_BASE_URL}${Endpoint.ATTACHMENT_URL}/upload"
            ) {
                setBody(
                    MultiPartFormDataContent(
                    formData {
                        append(
                            "file",
                            attachment.fileByteArray,
                            Headers.build {
                                append(HttpHeaders.ContentType, "images/*") // Mime type required
                                append(HttpHeaders.ContentDisposition, "filename=${attachment.fileName}") // Filename in content disposition required
                            }
                        )
                        append(
                            "captionText",
                            attachment.captionText
                        )
                    })
                )
            }
        }

    override suspend fun uploadFile(imageBytes: ByteArray, fileName: String, captionText: String): NetworkResponse<Int> =
        getSafeNetworkResponse {
            httpClient.post(
                urlString = "${Endpoint.SPRING_BOOT_BASE_URL}${Endpoint.ATTACHMENT_URL}/upload"
            ) {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append(
                                "file",
                                imageBytes,
                                Headers.build {
                                    append(HttpHeaders.ContentType, "images/*") // Mime type required
                                    append(HttpHeaders.ContentDisposition, "filename=${fileName}") // Filename in content disposition required
                                }
                            )
                            append(
                                "captionText",
                                captionText
                            )
                        })
                )
            }
        }

    override suspend fun downloadFile(id: Int): NetworkResponse<ByteArray> =
        getSafeNetworkResponse {

            httpClient.get(
                urlString = "${Endpoint.SPRING_BOOT_BASE_URL}${Endpoint.ATTACHMENT_URL}/$id"
            ) {
                contentType(ContentType.Application.OctetStream)
            }
        }
}