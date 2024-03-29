package com.mutualmobile.harvestKmp.data.network.chat.impl

import com.mutualmobile.harvestKmp.data.network.Endpoint.Params.SOCKET_URL
import com.mutualmobile.harvestKmp.data.network.chat.RealtimeMessagingClient
import com.mutualmobile.harvestKmp.domain.model.request.ChatMessageRequest
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class KtorRealtimeMessagingClient(
    private val client: HttpClient
) : RealtimeMessagingClient{

    private var session: WebSocketSession? = null
    override fun getStateStream(): Flow<String> {
        return flow {
            session = client.webSocketSession {
                url(SOCKET_URL)
            }
            val messageStates = session!!
                .incoming
                .consumeAsFlow()
                .filterIsInstance<Frame.Text>()
                .mapNotNull {
                    it.readText()
                }

            emitAll(messageStates)
        }
    }

    override suspend fun sendAction(action: ChatMessageRequest) {
        session?.outgoing?.send(
            Frame.Text(Json.encodeToString(action))
        )
    }

    override suspend fun close() {
        session?.close()
        session = null
    }
}