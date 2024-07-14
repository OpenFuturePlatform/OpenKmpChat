package com.mutualmobile.harvestKmp.data.network.chat.impl

import com.mutualmobile.harvestKmp.data.network.Endpoint
import com.mutualmobile.harvestKmp.data.network.chat.TaskApi
import com.mutualmobile.harvestKmp.data.network.getSafeNetworkResponse
import com.mutualmobile.harvestKmp.domain.model.request.TaskRequest
import com.mutualmobile.harvestKmp.domain.model.response.TaskResponse
import com.mutualmobile.harvestKmp.features.NetworkResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class TaskApiImpl(
    private val httpClient: HttpClient
) : TaskApi {
    override suspend fun get(username: String): NetworkResponse<List<TaskResponse>> =
        getSafeNetworkResponse {
            httpClient.get(
                urlString = "${Endpoint.SPRING_BOOT_BASE_URL}${Endpoint.TASK_URL}"
            ) {
                contentType(ContentType.Application.Json)
            }
        }

    override suspend fun save(taskRequest: TaskRequest): NetworkResponse<TaskResponse> =
        getSafeNetworkResponse {
            httpClient.post(
                urlString = "${Endpoint.SPRING_BOOT_BASE_URL}${Endpoint.TASK_URL}"
            ) {
                contentType(ContentType.Application.Json)
                setBody(taskRequest)
            }
        }
}