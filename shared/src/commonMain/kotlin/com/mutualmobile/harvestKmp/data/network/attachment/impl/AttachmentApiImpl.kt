package com.mutualmobile.harvestKmp.data.network.attachment.impl

import com.mutualmobile.harvestKmp.data.network.Endpoint
import com.mutualmobile.harvestKmp.data.network.attachment.AttachmentApi
import com.mutualmobile.harvestKmp.data.network.getSafeNetworkResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

class AttachmentApiImpl(
    private val httpClient: HttpClient
) : AttachmentApi {
    override suspend fun uploadFile(imageBytes: ByteArray, fileName: String): NetworkResponse<String> =
        getSafeNetworkResponse {
            httpClient.post(
                urlString = "${Endpoint.SPRING_BOOT_BASE_URL}${Endpoint.ATTACHMENT_URL}"
            ) {
                setBody(
                    MultiPartFormDataContent(
                    formData {
                        append(
                            "file",
                            imageBytes,
                            Headers.build {
                                append(HttpHeaders.ContentType, "images/*") // Mime type required
                                append(HttpHeaders.ContentDisposition, "filename=$fileName") // Filename in content disposition required
                            }
                        )
                    })
                )
            }
        }
}